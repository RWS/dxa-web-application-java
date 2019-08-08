pipeline {
    agent {
        node { label 'linux&&docker' }
    }

    stages {

        stage ('Build with JDK11') {
            steps {
                //DXA has to be able to be built on JDK11:
                withDockerContainer("maven:3.6-jdk-11-slim") { 
                    //Sometime in the future these maven-settings should not be needed here (model service should build without acces to SDL repositories)
                    withCredentials([file(credentialsId: 'dxa-maven-settings', variable: 'MAVEN_SETTINGS_PATH')]) {
                        script {
                            //DXA has to be able to be build without SDL proprietary dependencies:
                            //sh "mvn -B dependency:purge-local-repository -DreResolve=false"

                            sh "mvn -s $MAVEN_SETTINGS_PATH -B clean verify"

                            //Build the webapp:
                            sh "mvn -B -s $MAVEN_SETTINGS_PATH clean source:jar verify -f dxa-webapp/pom.xml"
                        }
                    }
                }
            }
        }

        stage('Create the docker builder image(s)') {
            steps {
                script {
                    jdk8BuilderImage = docker.image("maven:3.6-jdk-8-alpine")
                }
            }
        }

        stage('Build a branch') {
            when { not { branch 'develop' } }
            // Not on the develop branch, so build it, but do not install it.
            steps {
                withCredentials([file(credentialsId: 'dxa-maven-settings', variable: 'MAVEN_SETTINGS_PATH')]) {
                    script {
                        //Build on JDK8
                        jdk8BuilderImage.inside {
                            //Cleanup:
                            sh "rm -fr docs dxa-webapp.war"

                            //Main build:
                            sh "mvn -B -s $MAVEN_SETTINGS_PATH -Dmaven.repo.local=local-project-repo -Plocal-repository clean source:jar install javadoc:aggregate@publicApi"

                            //Build the webapp:
                            sh "mvn -B -s $MAVEN_SETTINGS_PATH -Dmaven.repo.local=local-project-repo -Plocal-repository clean source:jar install -f dxa-webapp/pom.xml"

                            //Build the archetype:
                            sh "mvn -B -s $MAVEN_SETTINGS_PATH -Dmaven.repo.local=local-project-repo -Plocal-repository clean source:jar install -f dxa-webapp/target/generated-sources/archetype/pom.xml"

                            //Move stuff around for archiving:
                            sh "mv target/site/publicApi/apidocs/ ./docs"
                            sh "mv dxa-webapp/target/dxa-webapp.war ."
                        }
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: "local-project-repo/**,dxa-webapp.war,docs/**"
                }
            }
        }


        stage('Build and deploy from develop') {
            when { branch 'develop' }
            steps {
                withCredentials([file(credentialsId: 'dxa-maven-settings', variable: 'MAVEN_SETTINGS_PATH')]) {
                    script {
                        //Build on JDK8 and deploy it to local repository:
                        jdk8BuilderImage.inside {
                            //Cleanup:
                            sh "rm -fr docs dxa-webapp.war"

                            //Main build:
                            sh "mvn -B -s $MAVEN_SETTINGS_PATH -Dmaven.repo.local=local-project-repo -Plocal-repository clean source:jar deploy javadoc:aggregate@publicApi"

                            //Build the webapp:
                            sh "mvn -B -s $MAVEN_SETTINGS_PATH -Dmaven.repo.local=local-project-repo -Plocal-repository clean source:jar install -f dxa-webapp/pom.xml"

                            //Build the archetype:
                            sh "mvn -B -s $MAVEN_SETTINGS_PATH -Dmaven.repo.local=local-project-repo -Plocal-repository clean source:jar install -f dxa-webapp/target/generated-sources/archetype/pom.xml "

                            //Move stuff around for archiving:
                            sh "mv target/site/publicApi/apidocs/ ./docs"
                            sh "mv dxa-webapp/target/dxa-webapp.war ."
                        }
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: "local-project-repo/**,dxa-webapp.war,docs/**"
                }
            }
        }
    }
}
