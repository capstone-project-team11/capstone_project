# nodejs-mysql restapi 만들기

## DB Design

![DB-Design](https://user-images.githubusercontent.com/48249549/81720410-54282880-94b9-11ea-8321-81eafe788e42.png)

## nodejs-express

- express로 restapi 구현
- mysql과 연동
- route 연습

![commit1](https://user-images.githubusercontent.com/48249549/81720455-699d5280-94b9-11ea-8d13-bec83d28e9f2.png)

---

## 9-10 주차 Progress

### NodeJS, mysql를 이용한 Restful API 구현 

https://github.com/Err0rCode7/nodejs-httpserver 

---
#### Used Lib
- express
- multer
- mime
- fs
- mysql
- cors
- bodyParser
- path

---
#### 구현된 RestAPI - End Point

(2020/05/18 ~ 2020/05/27)

---

- users 
    - Get/ `./users/`
    - Response JSON about all of users 

        ![image](https://user-images.githubusercontent.com/48249549/83009436-bb191600-a051-11ea-90bf-ddceab46c525.png)

    - Get/ `./users/:id/` 
    - Response JSON about a user

        ![image](https://user-images.githubusercontent.com/48249549/83009360-991f9380-a051-11ea-96f6-38c142a5fd7e.png)

    - Post/ `./users/` 
    - Register a user ( Inster a user ) 

        ![image](https://user-images.githubusercontent.com/48249549/83010262-f23bf700-a052-11ea-9d1a-b228d7dba562.png)

    - Put/ `./users/` 
    - Update data of a user 

        ![image](https://user-images.githubusercontent.com/48249549/83010799-ca00c800-a053-11ea-9157-bc7a4133978b.png)

    - Delete/ `./users/` 
    - Delete a user 

        ![image](https://user-images.githubusercontent.com/48249549/83010614-7d1cf180-a053-11ea-9708-f793fbabf7d1.png) 

        ![image](https://user-images.githubusercontent.com/48249549/83009810-472b3d80-a052-11ea-8b82-b0fa405eecbf.png)
    ---
    - Post/ `./users/auth/` 
    - Check authorization(login) of a user 
    - [Android](https://github.com/Err0rCode7/capsule-time-android) 에서 테스트 완료

- contents ( image or video )

    - Get/ `./contents/:contentId` 
    - Response content with content ID 

        ![image](https://user-images.githubusercontent.com/48249549/83011078-4693a680-a054-11ea-9e0a-2ee000d7607d.png)

    - Get/ `contents/capsule-id/:capsuleId` 
    - Response contentID 
    
    - Post/ `./contents/` 
    - Upload a content or a nubmer of contents 
    - with multipart/form-data

        ![image](https://user-images.githubusercontent.com/48249549/83015039-c58bdd80-a05a-11ea-9604-dcc080c1c159.png)

    - Put/ `./contents/`    Dont need 
    - Dont develope

    - Delete/ `./contents/contentId` 
    - Delete a content 


### Kotlin과 Java를 이용한 Login App Test 
https://github.com/Err0rCode7/capsule-time-android 

---
#### Used Lib
- Retrofit2

---
#### 구현된 UI와 기능

- login Test

    ![image](https://user-images.githubusercontent.com/48249549/83012171-01707400-a056-11ea-9f21-e1445105264f.png)

- SignUp( SignUP 클릭시 Intent 이동 ) Test

    ![image](https://user-images.githubusercontent.com/48249549/83012283-24028d00-a056-11ea-8903-81ce1ec64a5a.png)


#### commit 활동 내역

- RestAPI
![image](https://user-images.githubusercontent.com/48249549/83013919-be63d000-a058-11ea-8477-bfbc0d252cad.png)

- Android
![image](https://user-images.githubusercontent.com/48249549/83014008-e2bfac80-a058-11ea-984d-81724054aeca.png)

---
