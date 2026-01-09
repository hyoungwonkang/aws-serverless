const awsConfig = {
    Auth: {
        Cognito: {
            userPoolId: 'ap-northeast-2_XXXXXXKIa',
            userPoolClientId: 'XXXXXXXXXXXXXXXXXXXXXXXnor',
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