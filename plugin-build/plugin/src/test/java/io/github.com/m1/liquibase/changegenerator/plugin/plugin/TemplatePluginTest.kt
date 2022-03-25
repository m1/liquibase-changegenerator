package io.github.m1.liquibase.changegenerator.plugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertNotNull
import org.junit.Test

class TemplatePluginTest {

    @Test
    fun `plugin is applied correctly to the project`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")

        assert(project.tasks.getByName("newChangeLog") is NewChangeLogTask)
    }

    @Test
    fun `extension liquibaseChangeGenerator is created correctly`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.m1.liquibase-change-generator")

        assertNotNull(project.extensions.getByName("liquibaseChangeGenerator"))
    }

    // @Test
    // fun `parameters are passed correctly from extension to task`() {
    //     val project = ProjectBuilder.builder().build()
    //     project.pluginManager.apply("io.github.m1.liquibase-change-generator")
    //     val aFile = File(project.projectDir, ".tmp")
    //     (project.extensions.getByName("liquibaseChangeGenerator") as TemplateExtension).apply {
    //         tag.set("a-sample-tag")
    //         message.set("just-a-message")
    //         outputFile.set(aFile)
    //     }
    //
    //     val task = project.tasks.getByName("templateExample") as TemplateExampleTask
    //
    //     assertEquals("a-sample-tag", task.tag.get())
    //     assertEquals("just-a-message", task.message.get())
    //     assertEquals(aFile, task.outputFile.get().asFile)
    // }
}
