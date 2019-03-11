// DXA Java framework build pipeline
// Allocation of node for execution of build steps
node("linux") {

        // Global variable for Maven location (being boostrapped alongside with Pipeline)
        def lpr = pwd()+"/build/local-project-repo"

        timestamps { // Enable timestamping of every line in pipeline execution log
            stage("Checkout web-application-java repo") {
                dir("build/web-application-java") {
                    checkout scm
                }
            }
            stage("Build") {
                dir("build/web-application-java") {
                        // Inject settings.xml to Maven as secret file located in credential storage in Jenkins
                        withCredentials([file(credentialsId: 'dxa-maven-settings', variable: 'MAVEN_SETTINGS_PATH')]) {
			    sh "mvn --settings $MAVEN_SETTINGS_PATH org.jacoco:jacoco-maven-plugin:prepare-agent install javadoc:jar -Dmaven.repo.local=${lpr}"
                        }
                }
            }
            stage("Maven javadoc:aggregate") {
                dir("build/web-application-java") {
                        // Inject settings.xml to Maven as secret file located in credential storage in Jenkins
                        withCredentials([file(credentialsId: 'dxa-maven-settings', variable: 'MAVEN_SETTINGS_PATH')]) {
                            sh "mvn -f dxa-framework/pom.xml -Dmaven.repo.local=${lpr} javadoc:aggregate@publicApi"
                        }
                }
            }
            stage("Archive artefacts") {
                    archiveArtifacts artifacts: "local-project-repo/**,not-public-repo/**,dxa-webapp.war,docs/**", excludes: 'target/**/local-project-repo/**/*,target/**/gradle/**/*,target/**/.gradle/**/*,target/**/*-javadoc.jar,target/**/*-sources.jar'
            }
/*
            stage("Build-webapp") {
                def buildFolder = pwd()+"/build"
                    withEnv([
                        "installer_path=${buildFolder}/installer",
                        "webapp_folder=${buildFolder}/web-application-java/dxa-webapp", 
                        "maven_repo_local=${lpr}", 
                        "modules=core,context-expressions,search,googleanalytics,mediamanager,51degrees,smarttarget,audience-manager,ugc,tridion-docs-mashup",
                        "mvn_profiles=%modules:,=-module,%-module",  // $env:profiles is reserved by powershell!
                        "all_modules=%modules%,cid,test,tridion-docs",
                        "all_profiles=%all_modules:,=-module,%-module"
                        ]) {
                            powershell 'build-automation/src/main/resources/Build-WebApplicationJava.ps1'
                        }
            }
            stage("Build-webapp-archetype-compare") {
                def buildFolder = pwd()+"/build"
                    withEnv([
                        "maven_repo_local=${lpr}", 
                        "webapp_folder=${buildFolder}/web-application-java/dxa-webapp", 
                        "framework_folder=${buildFolder}/web-application-java/dxa-framework", 
                        "installer_path=${buildFolder}/installer"
                        ]) {
                            bat 'build-automation/src/main/resources/copy-build-results-java.cmd'
                        }
            }
             stage("Trigger model-service build") {
                    def brName = env.BRANCH_NAME.split('/')[-1]
                    if (brName.contains("PR-")) {
                        echo "WARNING: This is pull-request branch. Model service wouldn't be triggered"
                    } else {
                        try {
                            build job: "stash/${brName}/model_service", parameters: [booleanParam(name: 'deploy', value: true)], propagate: false, wait: false
                        }
                        catch(Exception e) {
                            echo "WARNING: No Job stash/${brName}/model_service available to trigger, proceeding with develop"
                            build job: 'stash/develop/model_service', parameters: [booleanParam(name: 'deploy', value: true)], propagate: false, wait: false
                        }
                    }
                }
*/
        }
}
