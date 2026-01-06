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
    try:
        # 1️⃣ 클라이언트에서 accessToken 받기
        access_token = req.data.get("accessToken")

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

        kakao_user_id = kakao_data.get("id")
        if not kakao_user_id:
            logging.error("Kakao user ID not found in response")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
                message="Kakao user ID not found"
            )

        uid = f"kakao_{kakao_user_id}"
        custom_token = auth.create_custom_token(uid)

        return {
            "customToken": custom_token.decode("utf-8"),
            "email": email,
            "nickname": nickname,
            "profileUri": profile_image,
         }

    except Exception as e:
        logging.exception(f"Exception in kakao_auth: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message=f"Internal error: {e}"
        )

@https_fn.on_call(region = "us-central1")
def naver_auth(req: https_fn.CallableRequest):
    try:
        access_token = req.data.get("accessToken")

        if not access_token:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.INVALID_ARGUMENT,
                message="accessToken is required"
            )

        # 2️⃣ 네이버 API 호출
        response = requests.get(
            "https://openapi.naver.com/v1/nid/me",
            headers={
                "Authorization": f"Bearer {access_token}"
            }
        )

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


        uid = f"naver_{naver_id}"
        custom_token = auth.create_custom_token(uid)

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

@https_fn.on_call(region = "us-central1")
def make_kakao_custom_token(req: https_fn.CallableRequest):
    try:
        # 1️⃣ 클라이언트에서 accessToken 받기
        access_token = req.data.get("accessToken")

        if not access_token:
            logging.warning("No accessToken received from client")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.INVALID_ARGUMENT,
                message="accessToken is required"
            )

        # 2️⃣ 카카오 API 호출
        response = requests.get(
            "https://kapi.kakao.com/v2/user/me",
            headers={"Authorization": f"Bearer {access_token}"}
        )

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

        kakao_user_id = kakao_data.get("id")
        if not kakao_user_id:
            logging.error("Kakao user ID not found in response")
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.UNAUTHENTICATED,
                message="Kakao user ID not found"
            )

        uid = f"kakao_{kakao_user_id}"
        custom_token = auth.create_custom_token(uid)

        return {"customToken": custom_token.decode("utf-8") }

    except Exception as e:
        logging.exception(f"Exception in kakao_auth: {e}")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message=f"Internal error: {e}"
        )


@https_fn.on_call(region = "us-central1")
def make_naver_custom_token(req: https_fn.CallableRequest):
    try:
        access_token = req.data.get("accessToken")

        if not access_token:
            raise https_fn.HttpsError(
                code=https_fn.FunctionsErrorCode.INVALID_ARGUMENT,
                message="accessToken is required"
            )

        # 2️⃣ 네이버 API 호출
        response = requests.get(
            "https://openapi.naver.com/v1/nid/me",
            headers={
                "Authorization": f"Bearer {access_token}"
            }
        )

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

        uid = f"naver_{naver_id}"
        custom_token = auth.create_custom_token(uid)

        return {"customToken": custom_token.decode("utf-8")    }

    except https_fn.HttpsError:
        raise
    except Exception as e:
        logging.exception("Unhandled exception in naver_auth")
        raise https_fn.HttpsError(
            code=https_fn.FunctionsErrorCode.INTERNAL,
            message=str(e)
        )