
package org.eclipse.m2e.tests.project;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.internal.MavenPluginActivator;
import org.eclipse.m2e.core.internal.project.registry.ProjectRegistryManager;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.MavenUpdateRequest;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ILifecycleMapping;
import org.eclipse.m2e.core.project.configurator.MojoExecutionKey;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.RequireMavenExecutionContext;


@RequireMavenExecutionContext
public class MavenProjectFacadeTest extends AbstractMavenProjectTestCase {

  public void testGetMojoExecution() throws Exception {
    IProject project = importProject("projects/getmojoexecution/pom.xml");
    assertNoErrors(project);

    IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, monitor);

    MojoExecutionKey compileKey = null;
    for(MojoExecutionKey executionKey : facade.getMojoExecutionMapping().keySet()) {
      if("maven-compiler-plugin".equals(executionKey.getArtifactId()) && "compile".equals(executionKey.getGoal())) {
        compileKey = executionKey;
        break;
      }
    }

    assertNotNull(compileKey);
    MojoExecution compileMojo = facade.getMojoExecution(compileKey, monitor);

    final IMaven maven = MavenPlugin.getMaven();
    final MavenProject mavenProject = facade.getMavenProject(monitor);

    assertEquals("1.5", maven.getMojoParameterValue(mavenProject, compileMojo, "source", String.class, monitor));
    assertEquals("1.6", maven.getMojoParameterValue(mavenProject, compileMojo, "target", String.class, monitor));
  }

  public void testGetMojoExecutionAfterWorkspaceRestart() throws Exception {
    IProject project = importProject("projects/getmojoexecution/pom.xml");
    assertNoErrors(project);

    IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
    deserializeFromWorkspaceState(facade);

    MojoExecutionKey compileKey = null;
    for(MojoExecutionKey executionKey : facade.getMojoExecutionMapping().keySet()) {
      if("maven-compiler-plugin".equals(executionKey.getArtifactId()) && "compile".equals(executionKey.getGoal())) {
        compileKey = executionKey;
        break;
      }
    }

    assertNotNull(compileKey);
    MojoExecution compileMojo = facade.getMojoExecution(compileKey, monitor);

    final IMaven maven = MavenPlugin.getMaven();
    final MavenProject mavenProject = facade.getMavenProject(monitor);

    assertEquals("1.5", maven.getMojoParameterValue(mavenProject, compileMojo, "source", String.class, monitor));
    assertEquals("1.6", maven.getMojoParameterValue(mavenProject, compileMojo, "target", String.class, monitor));
  }

  public void testGetMojoExecutions() throws Exception {
    IProject project = importProject("projects/getmojoexecution/pom.xml");
    assertNoErrors(project);

    IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, monitor);

    List<MojoExecution> executions = facade.getMojoExecutions("org.apache.maven.plugins", "maven-compiler-plugin",
        monitor, "compile");
    assertEquals(executions.toString(), 1, executions.size());

    final IMaven maven = MavenPlugin.getMaven();
    final MavenProject mavenProject = facade.getMavenProject(monitor);

    assertEquals("1.5", maven.getMojoParameterValue(mavenProject, executions.get(0), "source", String.class, monitor));
    assertEquals("1.6", maven.getMojoParameterValue(mavenProject, executions.get(0), "target", String.class, monitor));
  }

  @RequireMavenExecutionContext(require = false)
  public void testGetMojoExecutionsWithoutMavenExecutionContext() throws Exception {
    IProject project = importProject("projects/getmojoexecution/pom.xml");
    assertNoErrors(project);

    IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, monitor);

    List<MojoExecution> executions = facade.getMojoExecutions("org.apache.maven.plugins", "maven-compiler-plugin",
        monitor, "compile");
    assertEquals(executions.toString(), 1, executions.size());

    // in backward compat mode MavenProject is not disposed
    assertSame(facade.getMavenProject(monitor), facade.getMavenProject(monitor));
  }

  public void testGetMojoExecutionsAfterWorkspaceRestart() throws Exception {
    IProject project = importProject("projects/getmojoexecution/pom.xml");
    assertNoErrors(project);

    IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
    deserializeFromWorkspaceState(facade);

    List<MojoExecution> executions = facade.getMojoExecutions("org.apache.maven.plugins", "maven-compiler-plugin",
        monitor, "compile");
    assertEquals(executions.toString(), 1, executions.size());

    final IMaven maven = MavenPlugin.getMaven();
    final MavenProject mavenProject = facade.getMavenProject(monitor);

    assertEquals("1.5", maven.getMojoParameterValue(mavenProject, executions.get(0), "source", String.class, monitor));
    assertEquals("1.6", maven.getMojoParameterValue(mavenProject, executions.get(0), "target", String.class, monitor));
  }

  public void testGetProjectConfigurators() throws Exception {
    IProject project = importProject("projects/getmojoexecution/pom.xml");
    assertNoErrors(project);

    IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
    assertNotNull("Expected not null MavenProjectFacade", facade);
    ILifecycleMapping lifecycleMapping = MavenPlugin.getProjectConfigurationManager().getLifecycleMapping(facade);
    assertNotNull("Expected not null LifecycleMapping", lifecycleMapping);

    List<AbstractProjectConfigurator> projectConfigurators = lifecycleMapping.getProjectConfigurators(facade, monitor);
    assertNotNull("Expected not null projectConfigurators", projectConfigurators);
    assertEquals(1, projectConfigurators.size());
  }

  public void testGetProjectConfiguratorsAfterWorkspaceRestart() throws Exception {
    IProject project = importProject("projects/getmojoexecution/pom.xml");
    assertNoErrors(project);

    IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
    assertNotNull("Expected not null MavenProjectFacade", facade);
    deserializeFromWorkspaceState(facade);

    ILifecycleMapping lifecycleMapping = MavenPlugin.getProjectConfigurationManager().getLifecycleMapping(facade);
    assertNotNull("Expected not null LifecycleMapping", lifecycleMapping);

    List<AbstractProjectConfigurator> projectConfigurators = lifecycleMapping.getProjectConfigurators(facade, monitor);
    assertNotNull("Expected not null projectConfigurators", projectConfigurators);
    assertEquals(1, projectConfigurators.size());
  }

  public void testIsStale() throws Exception {
    IProject project = importProject("projects/testIsStale/pom.xml");
    waitForJobsToComplete();
    assertNoErrors(project);

    testIsStale(project, "pom.xml");
    for(IPath filename : ProjectRegistryManager.METADATA_PATH) {
      MavenUpdateRequest updateRequest = new MavenUpdateRequest(project, true /*offline*/, false /*updateSnapshots*/);
      MavenPluginActivator.getDefault().getMavenProjectManagerImpl().refresh(updateRequest, monitor);

      testIsStale(project, filename.toString());
    }
  }

  public void test436668_EclipseProjectMetadataNotStale() throws Exception {
    IProject project = importProject("projects/testIsStale/pom.xml");
    waitForJobsToComplete();
    assertNoErrors(project);

    testIsNotStale(project, ".classpath");
    testIsNotStale(project, ".project");
  }

  private void testIsStale(IProject project, String filename) throws Exception {
    testStale(project, filename, true);
  }

  private void testIsNotStale(IProject project, String filename) throws Exception {
    testStale(project, filename, false);
  }

  private void testStale(IProject project, String filename, boolean staleIfFileModified) throws Exception {
    IMavenProjectFacade projectFacade = MavenPlugin.getMavenProjectRegistry().create(project, monitor);
    assertFalse("Expected not stale MavenProjectFacade before changing the " + filename + " file",
        projectFacade.isStale());

    project.getFile(filename).touch(monitor);
    if(staleIfFileModified) {
      assertTrue("Expected stale MavenProjectFacade after changing the " + filename + " file", projectFacade.isStale());
    } else {
      assertFalse("Expected not stale MavenProjectFacade after changing the " + filename + " file",
          projectFacade.isStale());
    }
  }
}
