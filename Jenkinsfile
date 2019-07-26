pipeline {
  agent any
  stages {
    stage('prepare-test') {
      when {
        branch 'test'
      }
      steps {
        git(url: env.gitUrl+'/'+env.projectName+'.git', branch: 'test', credentialsId: '6ed03dc02abc63041fb10f034f8434b93bec7cb0')
      }
    }
    stage('prepare-master') {
      when {
        branch 'master'
      }
      steps {
        git(url: env.gitUrl+'/'+env.projectName+'.git', branch: 'master', credentialsId: '6ed03dc02abc63041fb10f034f8434b93bec7cb0')
      }
    }
    stage('build-test') {
      when {
        branch 'test'
      }
      steps {
        sh 'gradle build publishMavenPublicationToNexusRepository -x Test'
      }
    }
    stage('build-master') {
      when {
        branch 'master'
      }
      steps {
        sh 'gradle build publishMavenPublicationToNexusRepository -Penv=pro -x Test'
      }
    }
  }
  environment {
    projectName = 'common'
    gitUrl = 'https://github.com/pengfanglin'
  }
}