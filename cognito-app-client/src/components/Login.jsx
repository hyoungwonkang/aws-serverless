import { signIn } from "aws-amplify/auth";
import { useState } from "react";

function Login({onLoginSuccess, switchToSignUp}) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        try {
            // await signIn() Amplify 내부 동작
            // 1. Cognito에게 username/password Secure Remote Password 암호화 보냄
            // 2. 로그인 성공 시 Cognito가 AccessToken, IDToken, RefreshToken을 클라이언트로 보냄
            // 3. 토큰을 Amplify가 받아서 로컬 스토리지에 저장
            const { isSignedIn } = await signIn({username, password})

            if (isSignedIn) {
                onLoginSuccess();
            }
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={styles.container}>
            <h2>로그인</h2>
            <form onSubmit={handleSubmit} style={styles.form}>
                <input
                placeholder="이메일"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                style={styles.input}
                />
                <input
                type="password"
                placeholder="비밀번호"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                style={styles.input}
                />
                <button type="submit" style={styles.button}>로그인</button>
            </form>
            {error && <p style={styles.error}>{error}</p>}
            
            <p style={{ marginTop: '20px' }}>
                계정이 없으신가요? <button onClick={switchToSignUp} style={styles.linkButton}>회원가입</button>
            </p>
        </div>
    );
}

const styles = {
  container: { padding: '40px', maxWidth: '400px', margin: '0 auto', textAlign: 'center', border: '1px solid #ddd', borderRadius: '8px' },
  form: { display: 'flex', flexDirection: 'column', gap: '10px' },
  input: { padding: '10px', fontSize: '16px' },
  button: { padding: '10px', backgroundColor: '#4CAF50', color: 'white', border: 'none', cursor: 'pointer' },
  linkButton: { background: 'none', border: 'none', color: 'blue', textDecoration: 'underline', cursor: 'pointer' },
  error: { color: 'red', marginTop: '10px' }
};

export default Login;