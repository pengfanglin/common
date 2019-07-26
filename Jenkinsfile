pipeline {
  agent any
  stages {
    stage('build-test') {
      steps {
        sh 'gradle build publishMavenPublicationToNexusRepository -x Test'
      }
    }
    stage('build-pro') {
      steps {
        sh 'gradle build publishMavenPublicationToNexusRepository -Penv=pro-x Test'
      }
    }
  }
}