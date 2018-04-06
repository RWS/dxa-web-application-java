// DXA Java framework build pipeline
// Allocation of node for execution of build steps
node("dxadocker") {
    // Global variable for location of local-project-repo
    def lpr = ""
    // Global variable for Maven location (being boostrapped alongside with Pipeline)
    lpr = pwd()+"\\build\\local-project-repo"
    timestamps { // Enable timestamping of every line in pipeline execution log
    stage("Checkout installation repo") { // Checkout initial repo > this stage should be removed in case this pipeline will be located in tsi/installation repo
        // Checkout of tsi/installation Stash project (temporarily done on develop branch, should be picked up on board by jenkins env variable in case of usage as multibranch)
        checkout([$class: 'GitSCM', branches: [[name: '*/develop']], browser: [$class: 'Stash', repoUrl: 'https://stash.sdl.com/scm/tsi/installation'], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '55722a63-b868-491a-a365-2084a0f984b1', url: 'https://stash.sdl.com/scm/tsi/installation.git']]])
    }
    stage("Checkout web-application-java repo") {
        dir("build\\web-application-java") {
            checkout scm
        }
    }
    stage("Checkout dxa-modules repo") {
        dir("build\\dxa-modules") {
            checkout([$class: 'GitSCM', branches: [[name: '*/develop']], browser: [$class: 'Stash', repoUrl: 'https://stash.sdl.com/projects/TSI/repos/dxa-modules-java'], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '55722a63-b868-491a-a365-2084a0f984b1', url: 'https://stash.sdl.com/scm/tsi/dxa-modules.git']]])
        }
    }
    stage("Gradle publishLocal") {
        dir("build\\web-application-java\\dxa-builder") {
            bat "gradlew.bat -Dmaven.repo.local=${lpr} publishLocal"
        }
    }
    stage("Gradle buildDxa") {
        dir("build\\web-application-java") {
                withCredentials([file(credentialsId: 'masterbuild-settings-xml', variable: 'mavensettings')]) {
                    // Inject settings.xml to Maven as secret file located in credential storage in Jenkins
                    powershell '$a = Get-Content -Raw -Path $env:mavensettings;Set-Content -Path C:\\maven\\conf\\settings.xml -Value $a'
                }
                bat "gradlew.bat -Pcommand=\"org.jacoco:jacoco-maven-plugin:prepare-agent install javadoc:jar -Pcoverage-per-test,local-m2-remote,nexus-sdl\" -PmavenProperties=\"-e -Dmaven.repo.local=${lpr}\" -Dmaven.repo.local=${lpr} -Pbatch buildDxa"
        }
    }
    stage("Maven javadoc:aggregate") {
        dir("build\\web-application-java") {
                bat "mvn -f dxa-framework\\pom.xml -Dmaven.repo.local=${lpr} javadoc:aggregate@publicApi"
        }
    }
    stage("Gradle buildModules") {
        dir("build\\dxa-modules\\webapp-java") {
                bat "gradlew.bat -Pcommand=\"install javadoc:jar -Pcoverage-per-test,local-m2-remote,nexus-sdl\" -PmavenProperties=\"-e -Dmaven.repo.local=${lpr}\" -Dmaven.repo.local=${lpr} -Pbatch buildModules"
        }
    }
    stage("Build-webapp-archetype-compare") {
        def buildFolder = pwd()+"\\build"
            withEnv([
                "maven_repo_local=${lpr}", 
                "webapp_folder=${buildFolder}\\web-application-java\\dxa-webapp", 
                "framework_folder=${buildFolder}\\web-application-java\\dxa-framework", 
                "installer_path=${buildFolder}\\installer"
                ]) {
                    bat 'build-automation\\src\\main\\resources\\copy-build-results-java.cmd'
                }
    }
    stage("Archive artefacts") {
            archiveArtifacts artifacts: "local-project-repo\\**,not-public-repo\\**,dxa-webapp.war,docs\\**", excludes: 'target\\**\\local-project-repo\\**\\*,target\\**\\gradle\\**\\*,target\\**\\.gradle\\**\\*,target\\**\\*-javadoc.jar,target\\**\\*-sources.jar'
    }
    stage("Trigger model-service build")
        build job: '../stash/develop/model_service', parameters: [booleanParam(name: 'deploy', value: true)], propagate: false, wait: false
    }
}