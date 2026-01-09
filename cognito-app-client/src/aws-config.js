const awsConfig = {
    Auth: {
        Cognito: {
            userPoolId: 'ap-northeast-2_s9V1pHKIa',
            userPoolClientId: '2hn7d4m3vmfvvt1mqqpg8udnor',
            loginWith: {
                email: true
            },
            signUpVerificationMethod: 'code',
            userAttributes: {
                email: {
                    required: true,
                }
            }
        }
    }
}

export default awsConfig;