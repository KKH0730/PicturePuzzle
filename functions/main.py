import logging
import firebase_admin
from firebase_admin import auth
import requests
from firebase_functions import https_fn

# 로깅 설정
logging.basicConfig(level=logging.INFO)

# Firebase Admin 초기화
try:
    firebase_admin.initialize_app()
    logging.info("Firebase Admin initialized successfully")
except Exception as e:
    logging.error(f"Firebase Admin initialization failed: {e}")
    raise

@https_fn.on_call(region = "us-central1")
def kakao_auth(req: https_fn.CallableRequest):
    logging.info("kakao_auth called")

    try:
        # 1️⃣ 클라이언트에서 accessToken 받기
        access_token = req.data.get("accessToken")
        logging.info(f"Received accessToken: {access_token}")

        if not access_token:
            logging.warning("No accessToken received from client")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.INVALID_ARGUMENT,
                message="accessToken is required"
            )

        # 2️⃣ 카카오 API 호출
        logging.info("Calling Kakao API /v2/user/me")
        response = requests.get(
            "https://kapi.kakao.com/v2/user/me",
            headers={"Authorization": f"Bearer {access_token}"}
        )
        logging.info(f"Kakao API response status: {response.status_code}")

        if response.status_code != 200:
            logging.error(f"Kakao API call failed: {response.text}")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
                message="Invalid Kakao token"
            )

        # 3️⃣ 사용자 ID 추출
        kakao_data = response.json()
        kakao_account = kakao_data.get("kakao_account", {})
        profile = kakao_account.get("profile", {})

        email = kakao_account.get("email")  # 없을 수도 있음
        nickname = profile.get("nickname")
        profile_image = profile.get("profile_image_url")
        logging.info(f"Kakao user data: {kakao_data}")

        kakao_user_id = kakao_data.get("id")
        if not kakao_user_id:
            logging.error("Kakao user ID not found in response")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
                message="Kakao user ID not found"
            )

        uid = f"kakao_{kakao_user_id}"
        logging.info(f"Generated Firebase UID: {uid}")

        # 4️⃣ Firebase Custom Token 생성
        custom_token = auth.create_custom_token(uid)
        logging.info(f"Custom token created for UID: {uid}")


        return {
            "customToken": custom_token.decode("utf-8"),
            "email": email,
            "nickname": nickname,
            "profileUri": profile_image,
         }

        # return {"customToken": custom_token.decode("utf-8")}

    except Exception as e:
        logging.exception(f"Exception in kakao_auth: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message=f"Internal error: {e}"
        )

@https_fn.on_call(region = "us-central1")
def naver_auth(req: https_fn.CallableRequest):
    logging.info("naver_auth called")

    try:
        # 1️⃣ accessToken 받기
        access_token = req.data.get("accessToken")
        logging.info(f"Received accessToken: {access_token}")

        if not access_token:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.INVALID_ARGUMENT,
                message="accessToken is required"
            )

        # 2️⃣ 네이버 API 호출
        logging.info("Calling Naver API /v1/nid/me")
        response = requests.get(
            "https://openapi.naver.com/v1/nid/me",
            headers={
                "Authorization": f"Bearer {access_token}"
            }
        )

        logging.info(f"Naver API status: {response.status_code}")
        logging.info(f"Naver API body: {response.text}")

        if response.status_code != 200:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
                message="Invalid Naver token"
            )

        data = response.json()

        if data.get("resultcode") != "00":
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
                message="Naver auth failed"
            )

        naver_user = data.get("response", {})
        naver_id = naver_user.get("id")

        if not naver_id:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
                message="Naver user id not found"
            )

        # 3️⃣ Firebase UID 생성
        uid = f"naver_{naver_id}"
        logging.info(f"Generated UID: {uid}")

        # 4️⃣ Custom Token 생성
        custom_token = auth.create_custom_token(uid)
        logging.info("Custom token created")

        # (선택) 클라이언트로 기본 프로필도 같이 반환
        return {
            "customToken": custom_token.decode("utf-8"),
            "email": naver_user.get("email"),
            "nickname": naver_user.get("name"),
            "profileUri": naver_user.get("profile_image")
        }

    except https_fn.HttpsError:
        raise
    except Exception as e:
        logging.exception("Unhandled exception in naver_auth")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message=str(e)
        )