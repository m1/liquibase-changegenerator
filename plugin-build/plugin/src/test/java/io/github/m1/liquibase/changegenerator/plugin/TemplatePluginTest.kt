package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TemplatePluginTest {
    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")

        assert(project.tasks.getByName(NewChangeLogTask.TASK_NAME) is NewChangeLogTask)
    }

    @Test
    fun `extension liquibaseChangeGenerator is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")

        assertNotNull(project.extensions.getByName(EXTENSION_NAME))
    }

    @Test
    fun `parameters are passed correctly from extension to NewChangeLogTask`() {
        val rootPathEx = "./db"
        val changeLogFilePathEx = "./db/changelog.yml"
        val migrationsFolderEx = "migrations"

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")
        (project.extensions.getByName(EXTENSION_NAME) as ChangeGeneratorPluginExtension).apply {
            rootPath = rootPathEx
            changeLogFilePath = changeLogFilePathEx
            migrationsFolder = migrationsFolderEx
        }

        val task = project.tasks.getByName(NewChangeLogTask.TASK_NAME) as NewChangeLogTask

        assertEquals(rootPathEx, task.rootPath)
        assertEquals(changeLogFilePathEx, task.changeLogFilePath)
        assertEquals(migrationsFolderEx, task.migrationsFolder)
    }

    @Test
    fun `parameters are passed correctly from extension to NewChangeSetTask`() {
        val rootPathEx = "./db"
        val changeLogFilePathEx = "./db/changelog.yml"
        val migrationsFolderEx = "migrations"

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")
        (project.extensions.getByName(EXTENSION_NAME) as ChangeGeneratorPluginExtension).apply {
            rootPath = rootPathEx
            changeLogFilePath = changeLogFilePathEx
            migrationsFolder = migrationsFolderEx
        }

        val task = project.tasks.getByName(NewChangeSetTask.TASK_NAME) as NewChangeSetTask

        assertEquals(rootPathEx, task.rootPath)
        assertEquals(changeLogFilePathEx, task.changeLogFilePath)
        assertEquals(migrationsFolderEx, task.migrationsFolder)
    }

    @Test
    fun `default parameters are used if none set for NewChangeLogTask`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")
        project.extensions.getByName(EXTENSION_NAME) as ChangeGeneratorPluginExtension

        val defaultExtension = object : ChangeGeneratorPluginExtension(project) {}
        val task = project.tasks.getByName(NewChangeLogTask.TASK_NAME) as NewChangeLogTask

        assertEquals(defaultExtension.rootPath, task.rootPath)
        assertEquals(defaultExtension.changeLogFilePath, task.changeLogFilePath)
        assertEquals(defaultExtension.migrationsFolder, task.migrationsFolder)
    }

    @Test
    fun `default parameters are used if none set for NewChangeSetTask`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")
        project.extensions.getByName(EXTENSION_NAME) as ChangeGeneratorPluginExtension

        val defaultExtension = object : ChangeGeneratorPluginExtension(project) {}
        val task = project.tasks.getByName(NewChangeSetTask.TASK_NAME) as NewChangeSetTask

        assertEquals(defaultExtension.rootPath, task.rootPath)
        assertEquals(defaultExtension.changeLogFilePath, task.changeLogFilePath)
        assertEquals(defaultExtension.migrationsFolder, task.migrationsFolder)
    }

    @Test
    fun `input variables are set for NewChangeLogTask`() {
        val releaseVersionEx = "1.0.0"
        val releaseMajorEx = true
        val releaseMinorEx = true
        val releasePatchEx = true

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")
        project.extensions.getByName(EXTENSION_NAME) as ChangeGeneratorPluginExtension

        val task = project.tasks.getByName(NewChangeLogTask.TASK_NAME) as NewChangeLogTask
        task.setProperty("releaseVersion", releaseVersionEx)
        task.setProperty("releaseMajor", releaseMajorEx)
        task.setProperty("releaseMinor", releaseMinorEx)
        task.setProperty("releasePatch", releasePatchEx)

        assertEquals(releaseVersionEx, task.releaseVersion)
        assertEquals(releaseMajorEx, task.releaseMajor)
        assertEquals(releaseMinorEx, task.releaseMinor)
        assertEquals(releasePatchEx, task.releasePatch)
    }

    @Test
    fun `input variables are set for NewChangeSetTask`() {
        val changeSetNameEx = "changesetEx"
        val withTestdataEx = true

        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")
        project.extensions.getByName(EXTENSION_NAME) as ChangeGeneratorPluginExtension

        val task = project.tasks.getByName(NewChangeSetTask.TASK_NAME) as NewChangeSetTask
        task.setProperty("changeSetName", changeSetNameEx)
        task.setProperty("withTestdata", withTestdataEx)

        assertEquals(changeSetNameEx, task.changeSetName)
        assertEquals(withTestdataEx, task.withTestdata)
    }

    @Test
    fun `default input variables are set for NewChangeSetTask`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")
        project.extensions.getByName(EXTENSION_NAME) as ChangeGeneratorPluginExtension

        val task = project.tasks.getByName(NewChangeSetTask.TASK_NAME) as NewChangeSetTask
        val defaultTask = project.tasks.getByName(NewChangeSetTask.TASK_NAME) as NewChangeSetTask

        assertEquals(defaultTask.changeSetName, task.changeSetName)
        assertNotNull(task.changeSetName)
    }
}
