import { confirmSignUp, signUp } from "aws-amplify/auth";
import { useState } from "react";

function SignUp({switchToLogin}) {
    const [step, setStep] = useState('form');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [code, setCode] = useState('');
    const [error, setError] = useState('');

    // 회원가입 요청
    const handleSignUp = async (e) => {
        e.preventDefault();
        setError('');

        try {
            await signUp({
                username, password,
                options: {
                    userAttributes: {
                        email: username
                    }
                }
            });
            setStep('confirm');
            alert("인증 코드가 이메일로 발송되었습니다!")
        } catch (err) {
            setError(err.message);
        }
    };

    // 인증 코드 확인
    const handleConfirm = async (e) => {
        e.preventDefault();
        setError('');

        try {
            await confirmSignUp({
                username, confirmationCode: code
            });
            alert("회원가입 완료! 로그인해주세요.");
            switchToLogin();
        } catch (err) {
            setError(err.message);
        }
    };

    return (
        <div style={styles.container}>
            <h2>{step === 'form' ? '회원가입' : '이메일 인증'}</h2>
            
            {step === 'form' ? (
                <form onSubmit={handleSignUp} style={styles.form}>
                    <input
                        placeholder="이메일"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        style={styles.input}
                    />
                    <input
                        type="password"
                        placeholder="비밀번호 (8자 이상)"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        style={styles.input}
                    />
                    <button type="submit" style={styles.button}>인증 코드 받기</button>
                </form>
            ) : (
                <form onSubmit={handleConfirm} style={styles.form}>
                    <p>{username}로 발송된 6자리 코드를 입력하세요.</p>
                    <input
                        placeholder="인증 코드 (예: 123456)"
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        style={styles.input}
                    />
                    <button type="submit" style={styles.button}>가입 완료</button>
                </form>
            )}

            {error && <p style={styles.error}>{error}</p>}
            
            <p style={{ marginTop: '20px' }}>
                이미 계정이 있으신가요? <button onClick={switchToLogin} style={styles.linkButton}>로그인</button>
            </p>
        </div>
    );
}

const styles = {
  container: { padding: '40px', maxWidth: '400px', margin: '0 auto', textAlign: 'center', border: '1px solid #ddd', borderRadius: '8px' },
  form: { display: 'flex', flexDirection: 'column', gap: '10px' },
  input: { padding: '10px', fontSize: '16px' },
  button: { padding: '10px', backgroundColor: '#2196F3', color: 'white', border: 'none', cursor: 'pointer' },
  linkButton: { background: 'none', border: 'none', color: 'blue', textDecoration: 'underline', cursor: 'pointer' },
  error: { color: 'red', marginTop: '10px' }
};

export default SignUp;