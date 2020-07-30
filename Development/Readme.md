# 개발 내용

### 1차 수정 By SeungWon ( 2020 / 7 / 21 )

#### Commit 내용 (수정 사항) 

---

1. 세션 기능을 추가하여 로그인시 세션을 발급 받고 SharedPreference에 저장, 세션이 유효한 유저에게만 어플 사용을 허가.
2. 로그인시  SharedPreference에 nick_name을 저장, 하단 네비바 이동시 nick_name이나 user 객체를 교환 하는 과정을 없애고 저장된 nick_name을 불러오고 서버로 부터 유저 정보 요청
3. 캡슐 맵에서 검색 페이지로 이동시 팅기는 버그 수정
4. 몇몇개의 불필요한 코드내용 제거
5. 추가가 필요하거나 부족한 부분 점검 및 테스트

#### 문제 사항 (수정이 필요한 사항) 

---

1. userpage - 뒤로가기 버튼 기능 작동 안함
2. userpage map - 버튼들 기능 작동 안함
3. 하단 네비바 아이콘 (검색, 지도) 변경 필요
4. 설정 기능 문제 ( 설정 페이지 이동 전에 사용자 재인증(로그인) 기능 부재, 이미지 변경 및 변경된 닉네임으로 마이페이지에 적용 필요 , 완료시 안내메시지와 페이지 이동, 로그아웃 기능)
5. 캡슐 로그 리스트에서 캡슐 클릭시 좋아요 보기 기능 부재
6. 한 유저가 다른 유저 페이지에 들어가거나 리스트를 보는 등에 상황에서 팔로우, 팔로우해제 하는 기능 부재
7. 한 유저가 다른 유저 페이지에 들어갔을 때 보이는 캡슐 로그 리스트들에 대한 제한 ( 논의하여 변경 )
8. 임시 캡슐 삭제기능 추가 필요
9. 팔로우 팔로워 설정 등의 버튼 눌러서 이동시 문제
    - 페이지 이동시 하단 네비바 이름 출력 제거 필요와 하단바 기능 작동 안함
    - 팔로우나 팔로워 리스트를 통해서 유저페이지로 들어갔을 때 지도에서 캡슐 클릭시 출력되는 이벤트 수정 필요 ( 논의하여 변경 )
10. 해쉬태그, 유저 검색시 문제
    - 간혹 동일한 닉네임이 두개 출력됨 ( 사진 첨부 1)
    - 자신의 닉네임이 검색됨 ( 사진 첨부 2)
    - 검색 내용 입력 후 내용을 지우면 검색에 매칭과 다른 유저가 검색됨 ( 사진 첨부 3)
	- 해쉬태그로 캡슐 검색후 어떻게 활용할 것인지 수정 필요 ( 논의하여 변경 )

##### 사진 첨부 1, 2
    -   ![Screenshot_1595310048](https://user-images.githubusercontent.com/48249549/88025501-5240ac80-cb6f-11ea-91b0-4d06e5f8ab65.png)
##### 사진 첨부 3
    -   ![Screenshot_1595313791](https://user-images.githubusercontent.com/48249549/88025511-566cca00-cb6f-11ea-9396-561e52d27808.png)

#### 유의 사항
1. 세션 추가로 인해 Retrofit으로 서버와 통신시 Retrofit_Client 객체에 Context를 파라미터로 넘겨줘야함
2. 서버와 통신할때 Response가 왔을 때 Response의 Header Code가 401인지 비교하여 True이면 로그인 페이지로 이동하는 조건문을 항상 맨위에 넣어줘야함 (또한, intent stack all clear 필요 )
3. 닉네임 변경시 SharedPreference (어플 내 저장 공간)에 저장되어있는 해쉬값 nick_name을 갱신해줘야함
4. 로그아웃 기능을 만들떄 SharedPreference에 저장된 해쉬값 nick_name을 제거해주고 로그인 페이지로 이동해줘야함 . (또한, intent stack all clear 필요 )

### 2차 수정 By SeungWon ( 2020 / 7 / 30 )

#### Commit 내용 (수정 사항)

1. 캡슐맵 - Filter 기능 추가
2. 캡슐맵 - Cluster 기능 및 아이콘 변경
3. Bug Fix 1 - My Map에서 Capsule Map 이동시 작동을 안하는 버그 수정
4. Bug Fix 2 - 잠금 캡슐 기능 보완 (이미지와 함께 잠금 캡슐 생성이 잘 되도록 수정)


![image](https://user-images.githubusercontent.com/48249549/88960554-aa4d8080-d2de-11ea-97a3-3eb729f50ca4.png)



#### 문제 사항 (수정이 필요한 사항)

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
