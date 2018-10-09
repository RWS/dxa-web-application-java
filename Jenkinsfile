def branchCheckout(stashUrl, branchName) {
    if (branchName.contains("PR-")) {
        throw new Exception('Pull Request Builds are not supported because this build uses multiple repositories ;(')
    }
    try {
        checkout(
            [
                $class: 'GitSCM', 
                branches: [
                    [ name: "*/${branchName}" ]
                ], 
                browser: [
                    $class: 'Stash', 
                    repoUrl: stashUrl
                ], 
                doGenerateSubmoduleConfigurations: false,
                extensions: [
                    [
                        $class: 'CloneOption',
                        shallow: true
                    ]
                ],
                submoduleCfg: [],
                userRemoteConfigs: [
                    [
                        credentialsId: '55722a63-b868-491a-a365-2084a0f984b1', 
                        url: "${stashUrl}.git"
                    ]
                ]
            ]
        )
    }
    catch(Exception e) {
        echo "WARNING: No branch ${branchName} present in '${ stashUrl.split('/')[-1] }' repo, proceeding with develop"
        checkout(
            [
                $class: 'GitSCM', 
                branches: [
                    [name: "*/develop"]
                ], 
                browser: [
                    $class: 'Stash', 
                    repoUrl: stashUrl
                ], 
                doGenerateSubmoduleConfigurations: false,
                extensions: [
                    [
                        $class: 'CloneOption',
                        shallow: true
                    ]
                ],
                submoduleCfg: [],
                userRemoteConfigs: [
                    [
                        credentialsId: '55722a63-b868-491a-a365-2084a0f984b1', 
                        url: "${stashUrl}.git"
                    ]
                ]
            ]
        )
    }
}

// DXA Java framework build pipeline
// Allocation of node for execution of build steps
node("dxadocker") {

///// Uncomment Try...Catch to retain containers for debugging.
//    try
//    {

        // Global variable for location of local-project-repo
        def lpr = ""
        // Global variable for Maven location (being boostrapped alongside with Pipeline)
        lpr = pwd()+"\\build\\local-project-repo"
        timestamps { // Enable timestamping of every line in pipeline execution log
            stage("Checkout installation repo") { // Checkout initial repo > this stage should be removed in case this pipeline will be located in tsi/installation repo
                branchCheckout('https://stash.sdl.com/scm/tsi/installation', env.BRANCH_NAME)
            }
            stage("Checkout web-application-java repo") {
                dir("build\\web-application-java") {
                    checkout scm
                }
            }
            stage("Checkout dxa-modules repo") {
                dir("build\\dxa-modules") {
                    branchCheckout('https://stash.sdl.com/scm/tsi/dxa-modules', env.BRANCH_NAME)
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
            stage("Build-webapp") {
                def buildFolder = pwd()+"\\build"
                    withEnv([
                        "installer_path=${buildFolder}\\installer",
                        "webapp_folder=${buildFolder}\\web-application-java\\dxa-webapp", 
                        "maven_repo_local=${lpr}", 
                        "modules=core,context-expressions,search,googleanalytics,mediamanager,51degrees,smarttarget,audience-manager,ugc,tridion-docs-mashup",
                        "mvn_profiles=%modules:,=-module,%-module",  // $env:profiles is reserved by powershell!
                        "all_modules=%modules%,cid,test,tridion-docs",
                        "all_profiles=%all_modules:,=-module,%-module"
                        ]) {
                            powershell 'build-automation\\src\\main\\resources\\Build-WebApplicationJava.ps1'
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
        }
//    } catch (Exception ex) {
//        def userInput = input(
//            id: 'userInput', message: 'Delete Container?', parameters: [
//            [$class: 'BooleanParameterDefinition', defaultValue: true, description: 'Any result to delete container.', name: 'del']
//            ])
//        throw new Exception('Build Broken')
//    } finally {
//        echo "Done!"
//    }
}