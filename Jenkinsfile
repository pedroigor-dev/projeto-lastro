pipeline {
    agent any
    tools {
        jdk 'temurin-21'
        nodejs 'node-22'
    }
    stages {
        stage('Backend quality') {
            steps { dir('backend') { bat 'mvnw.cmd --batch-mode verify' } }
        }
        stage('Frontend quality') {
            steps {
                dir('frontend') {
                    bat 'npm ci'
                    bat 'npm test -- --watch=false'
                    bat 'npm run build'
                }
            }
        }
        stage('SonarQube') {
            when { expression { env.CHANGE_ID != null } }
            steps {
                withSonarQubeEnv('sonarqube') {
                    bat 'backend\\mvnw.cmd -f backend\\pom.xml sonar:sonar'
                }
            }
        }
    }
    post {
        always { junit 'backend/target/surefire-reports/*.xml' }
    }
}
