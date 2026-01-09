import { fetchAuthSession } from "aws-amplify/auth";
import axios from "axios";

// Axios 인스턴스 생성 (기본 설정)
const axiosClient = axios.create({
  // API Gateway 주소 입력 (/users/me 같은 상세 경로는 제외)
  baseURL: 'https://kofiqwmrll.execute-api.ap-northeast-2.amazonaws.com/Prod',
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터 설정
axiosClient.interceptors.request.use(
    async (config) => {
        try {
            // 1. 로컬 스토리지에서 토큰 꺼내기
            // 2. 만료시간 체크. 만료 시 Cognito에게 새 accessToken 요청.
            // 3. 토큰의 묶음을 세션 형태로 반환 (서버의 세션은 아님. stateless session으로 표현할 뿐.)
            const session = await fetchAuthSession();
            const token = session.tokens?.idToken.toString();

            // 토큰을 요청 헤더에 추가
            if (token) {
                // 일반적인 Bearer 방식과 달리, API Gateway는 토큰만 요구
                config.headers.Authorization = token;
            }

            return config; // 수정된 설정으로 요청 계속 진행
        } catch (error) {
            // 토큰 가져오기 실패 시, 그냥 요청을 보냄
            return config;
        }
    },
    (error) => {
        return Promise.reject(error);
    }
)

export default axiosClient;