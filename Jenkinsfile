pipeline {
  agent {
    node {
      label 'dxadocker'
    }
  }
  stages {
    stage('Gradlew: publishLocal') {
      steps {
        timestamps() {
          bat 'mkdir local-project-repo'
          dir('dxa-builder') {
            bat 'gradlew.bat -Dmaven.repo.local=..\\local-project-repo publishLocal'
          }
        }
      }
    }
    stage('Gradlew: buildDxa') {
      steps {
        timestamps() {
          withCredentials([file(credentialsId: 'masterbuild-settings-xml', variable: 'mavensettings')]) {
            // Inject settings.xml to Maven as secret file located in credential storage in Jenkins
                    powershell '$a = Get-Content -Raw -Path $env:mavensettings;Set-Content -Path C:\\maven\\conf\\settings.xml -Value $a'
                    bat "gradlew.bat -Pcommand=\"org.jacoco:jacoco-maven-plugin:prepare-agent install javadoc:jar -Pcoverage-per-test,local-m2-remote,nexus-sdl\" -PmavenProperties=\"-e -Dmaven.repo.local=local-project-repo\" -Dmaven.repo.local=local-project-repo -Pbatch buildDxa"
          }
          }
        }
      }
    stage('Maven: javadoc:aggregate') {
      steps {
        timestamps() {
                    bat "mvn -f dxa-framework\\pom.xml -Dmaven.repo.local=local-project-repo javadoc:aggregate@publicApi"
          }
        }
      }
    stage('Upload Artefacts') {
      steps {
        timestamps() {
            dir ('artefact') {
              powershell returnStdout: false, script: 'Copy-Item -Force -Recurse ..\\local-project-repo .'
            }
            withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', accessKeyVariable: 'AWS_ACCESS_KEY_ID', credentialsId: 'aws-docker-registry', secretKeyVariable: 'AWS_SECRET_ACCESS_KEY']]) {
              dir('artefact') {
                bat "aws s3 cp --recursive --region eu-west-1 . s3://sdl-web-dev-dxa-artifacts/pipelines/webapp-java/%BRANCH_NAME%/%BUILD_ID%"
              }
            }
          script {
                currentBuild.description = "Artefacts uploaded to <a href='https://s3.eu-west-1.amazonaws.com/sdl-web-dev-dxa-artifacts/pipelines/webapp-java/${BRANCH_NAME}/${env.BUILD_ID}/'>s3://sdl-web-dev-dxa-artifacts/pipelines/webapp-java/${BRANCH_NAME}/${env.BUILD_ID}</a>"
              }
        }
      }
    }
  }
}