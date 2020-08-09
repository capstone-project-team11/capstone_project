# 2차 수정 By SeungWon ( 2020 / 7 / 30 )

## Commit 내용 (수정 사항)

1. 캡슐맵 - Filter 기능 추가
2. 캡슐맵 - Cluster 기능 및 아이콘 변경
3. Bug Fix 1 - My Map에서 Capsule Map 이동시 작동을 안하는 버그 수정
4. Bug Fix 2 - 잠금 캡슐 기능 보완 (이미지와 함께 잠금 캡슐 생성이 잘 되도록 수정)


![image](https://user-images.githubusercontent.com/48249549/88960554-aa4d8080-d2de-11ea-97a3-3eb729f50ca4.png)



## 문제 사항 (수정이 필요한 사항)

1. 잠금 캡슐 생성시 시간 미선택 (날짜만 선택 OR 시간만 선택 OR 선택 X) 등의 입력 값에 대한 예외 처리 부재, 추가적인 예외처리 추가 필요

2. My Page 또는 User Page의 캡슐로그 클릭 후 삭제 기능 수행시 오류 발생 (안드로이드 내부 오류 - 오류코드 첨부)

```bash
2020-07-31 03:10:18.097 30053-30053/com.example.capsuletime E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.example.capsuletime, PID: 30053
    java.lang.NullPointerException: Attempt to invoke virtual method 'void com.example.capsuletime.mainpages.mypage.CapsuleLogAdapter.remove(int)' on a null object reference
        at com.example.capsuletime.mainpages.mypage.dialogs.ViewCapsuleDialog$2$1$1.onResponse(ViewCapsuleDialog.java:185)
        at retrofit2.DefaultCallAdapterFactory$ExecutorCallbackCall$1.lambda$onResponse$0$DefaultCallAdapterFactory$ExecutorCallbackCall$1(DefaultCallAdapterFactory.java:89)
        at retrofit2.-$$Lambda$DefaultCallAdapterFactory$ExecutorCallbackCall$1$3wC8FyV4pyjrzrYL5U0mlYiviZw.run(Unknown Source:6)
        at android.os.Handler.handleCallback(Handler.java:883)
        at android.os.Handler.dispatchMessage(Handler.java:100)
        at android.os.Looper.loop(Looper.java:214)
        at android.app.ActivityThread.main(ActivityThread.java:7356)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:492)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:930)
```