import { useEffect, useState } from "react";
import { getCurrentUser } from "aws-amplify/auth";

import Login from "./components/Login";
import SignUp from "./components/SignUp";
import Dashboard from "./components/Dashboard";

function App() {
  const [view, setView] = useState('login');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkUser();
  }, []);

  async function checkUser() {
    try {
      await getCurrentUser();
      setView('dashboard'); // 로그인 되어있으면 대시보드로
    } catch {
      setView('login'); // 아니면 로그인 화면으로
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      {/* 상태에 따라 다른 컴포넌트 보여주기 */}
      {view === 'login' && (
        <Login
          onLoginSuccess={() => setView('dashboard')} 
          switchToSignUp={() => setView('signup')} 
        />
      )}

      {view === 'signup' && (
        <SignUp 
          switchToLogin={() => setView('login')} 
        />
      )}
      
      {view === 'dashboard' && (
        <Dashboard 
          onSignOut={() => setView('login')} 
        />
      )}
    </div>
  )
}

export default App;