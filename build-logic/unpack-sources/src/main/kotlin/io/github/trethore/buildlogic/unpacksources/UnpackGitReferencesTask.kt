package io.github.trethore.buildlogic.unpacksources

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class UnpackGitReferencesTask : DefaultTask() {
    @get:Input
    abstract val gitReferences: ListProperty<String>

    @get:Internal
    abstract val referencesDirectory: DirectoryProperty

    @get:Internal
    abstract val rootDirectory: DirectoryProperty

    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    @TaskAction
    fun unpack() {
        val referencesDir = referencesDirectory.get().asFile
        val rootDir = rootDirectory.get().asFile
        referencesDir.mkdirs()
        GitReferenceUnpacker(
            logger,
            rootDir,
            fileSystemOperations,
            CommandRunner(execOperations, rootDir),
        ).unpackAll(
            gitReferences.get().map(GitReference::deserialize),
            referencesDir,
        )
    }
}
