import { useState } from "react";
import axiosClient from "../axiosClient";
import { signOut } from "aws-amplify/auth";

function Dashboard({ onSignOut }) {
    const [apiResponse, setApiResponse] = useState('');

    const callApi = async () => {
        try {
            const response = await axiosClient.get('/users/me');
            setApiResponse(JSON.stringify(response.data, null, 2));
        } catch (error) {
            setApiResponse("Error: " + error.response?.data?.message || error.message);
        }
    };

    const handleSignOut = async () => {
        await signOut();
        onSignOut(); 
    }

    return (
        <div style={{ padding: '40px', maxWidth: '600px', margin: '0 auto' }}>
            <h1>환영합니다!</h1>
            <div style={{ border: '1px solid #ddd', padding: '20px', margin: '20px 0' }}>
                <button onClick={callApi} style={{ padding: '10px 20px', cursor: 'pointer' }}>
                API 호출하기
                </button>
                <pre style={{ background: '#f4f4f4', padding: '10px', marginTop: '10px' }}>
                {apiResponse || '결과 대기 중...'}
                </pre>
            </div>
            <button onClick={handleSignOut} style={{ padding: '10px', backgroundColor: '#ff4444', color: 'white', border: 'none' }}>
                로그아웃
            </button>
        </div>
    )
}

export default Dashboard;