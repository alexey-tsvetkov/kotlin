/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.gradle.tasks

import org.jetbrains.kotlin.com.intellij.util.io.BooleanDataDescriptor
import org.gradle.api.logging.Logging
import org.jetbrains.kotlin.build.GeneratedJvmClass
import org.jetbrains.kotlin.incremental.CompilationResult
import org.jetbrains.kotlin.incremental.IncrementalCacheImpl
import org.jetbrains.kotlin.incremental.dumpCollection
import org.jetbrains.kotlin.incremental.storage.BasicStringMap
import org.jetbrains.kotlin.incremental.storage.PathStringDescriptor
import org.jetbrains.kotlin.incremental.storage.StringCollectionExternalizer
import org.jetbrains.kotlin.modules.TargetId
import java.io.File

class GradleIncrementalCacheImpl(targetDataRoot: File, targetOutputDir: File?, target: TargetId) : IncrementalCacheImpl<TargetId>(targetDataRoot, targetOutputDir, target) {

    companion object {
        private val SOURCES_TO_CLASSFILES = "sources-to-classfiles"
        private val CLASSPATH = "classpath"
    }

    private val loggerInstance = Logging.getLogger(this.javaClass)
    fun getLogger() = loggerInstance

    private val sourceToClassfilesMap = registerMap(SourceToClassfilesMap(SOURCES_TO_CLASSFILES.storageFile))
    private val classpathMap = registerMap(ClasspathSet(CLASSPATH.storageFile))

    fun compareClasspath(currentClasspath: Set<File>): FileDifference {
        val added = currentClasspath.asSequence().filter { it !in classpathMap }
        val removed = classpathMap.values.asSequence().filter { it !in currentClasspath }
        return FileDifference(added, removed)
    }

    fun updateClasspath(currentClasspath: Set<File>) {
        classpathMap.clean()
        currentClasspath.forEach { classpathMap.add(it) }
    }

    fun removeClassfilesBySources(sources: Iterable<File>): Unit =
            sources.forEach { sourceToClassfilesMap.remove(it) }

    override fun saveFileToCache(generatedClass: GeneratedJvmClass<TargetId>): CompilationResult {
        generatedClass.sourceFiles.forEach { sourceToClassfilesMap.add(it, generatedClass.outputFile) }
        return super.saveFileToCache(generatedClass)
    }

    inner class SourceToClassfilesMap(storageFile: File) : BasicStringMap<Collection<String>>(storageFile, PathStringDescriptor, StringCollectionExternalizer) {
        fun add(sourceFile: File, classFile: File) {
            storage.append(sourceFile.absolutePath, classFile.absolutePath)
        }

        operator fun get(sourceFile: File): Collection<File> =
                storage[sourceFile.absolutePath].orEmpty().map { File(it) }

        override fun dumpValue(value: Collection<String>) = value.dumpCollection()

        fun remove(file: File) {
            // TODO: do it in the code that uses cache, since cache should not generally delete anything outside of it!
            // but for a moment it is an easiest solution to implement
            get(file).forEach {
                getLogger().debug("[KOTLIN] Deleting $it on clearing cache for $file")
                it.delete()
            }
            storage.remove(file.absolutePath)
        }
    }
}

class FileDifference(val added: Sequence<File>, val removed: Sequence<File>) {
    fun isNotEmpty(): Boolean =
            added.any() || removed.any()
}

private abstract class PersistentFileSet(storageFile: File) : BasicStringMap<Boolean>(storageFile, BooleanDataDescriptor.INSTANCE) {
    val values: Iterable<File>
        get() = storage.keys.map { File(it) }

    fun add(file: File) {
        storage[file.canonicalPath] = true
    }

    operator fun contains(file: File): Boolean =
            storage.contains(file.canonicalPath)

    override fun dumpValue(value: Boolean) = ""
}

private class ClasspathSet(storageFile: File) : PersistentFileSet(storageFile)