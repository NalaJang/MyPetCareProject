# My Pet Care - 반려동물 돌봄 서비스 어플
## My Pet Care 서비스를 통해 반려동물🐶과 반려인🙂의 걱정과 불안을 해소하세요!</br>
✔️ 집에 혼자 있는 반려동물이 걱정될 때</br>
✔️ 장시간 집을 비워두어야 할 때</br>
✔️ 특별한 돌봄이 필요할 때


## Table of contents
* About Project
* Technologies(개발 환경)
* Menu Tree
* 주요 기능
* 주요 기능 상세
* Reference


## About Project
* DB 활용 경험을 확대하기 위해 Firebase 사용
* kotlin 언어 사용을 목적으로 제작한 어플
* 개발 기간: 2022.8.22 ~ 2022.9.25


## Technologies
* Java 8
* OS: Mac
* 언어: Kotlin
* Android 버전: Bumblebee
* 최소 SDK 버전: 27
* 타겟 SDK 버전: 32
* Database: Firebase

## Menu Tree
<img width="469" alt="image" src="https://user-images.githubusercontent.com/73895803/193718379-2715d82c-0ca3-4359-9d0a-b2cd0aada5e7.png">


## 주요 기능
* 회원 가입
* 로그인
* 일정
* 채팅
* 설정

## 주요 기능 상세
#### 회원 가입
  * 사용자 정보와 반려 동물 정보 입력
  * 중복 아이디 
  * 가입 완료 시 Firebase에 계정 생성과 기본 프로필 저장
<details>
  <summary>Image</summary>
    
<img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193718550-56aced60-0576-4360-b17c-1838d6112f55.png">
</details>
  
***
  
#### 로그인
  * Firebase의 Authentication을 통해 사용자가 입력한 값과 비교해서 값이 일치할 때 로그인 허용
  * 로그인 성공 시 일정 화면으로 이동
<details>
  <summary>Image</summary>
    
<img width="274" alt="image" src="https://user-images.githubusercontent.com/73895803/193718223-e62aea2a-939b-4820-9d9a-318527cfe79c.png">
</details>
  
***

#### 일정
  <details>
    <summary>Image</summary>
    
<img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193718725-67526f27-dbb0-4fb6-8559-dbdbd150a1d4.png">
  </details>


  * 일정 신청
    * 날짜 선택 -> 신청 버튼 클릭 -> 신청하고자 하는 서비스 유형과 돌봄이 필요한 시간, 메모 내용 등을 입력 -> 완료 버튼 클릭
<details>
  <summary>Image</summary>
    
<img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193718792-3fb4675e-28ee-4a1f-8155-c7adda1a10da.png">
<img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193719107-7ef7378a-036c-4503-b997-7768aa867627.png">
</details>

 
  * 일정 확인
    * 일정이 있는 날짜 선택 -> 일정 클릭 -> 신청된 서비스 유형과 시간, 메모 내용, 담당 매니저를 확인할 수 있음
<details>
  <summary>Image</summary>
    
<img width="270" alt="image" src="https://user-images.githubusercontent.com/73895803/193719196-5232720c-2ba1-443b-b124-97dbb8b2cd40.png">
<img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193719504-865fb10e-b12c-4831-8e3b-3457e8cfa527.png">
</details>
    
***
    
<details>
  <summary>Image</summary>
    
<img width="272" alt="image" src="https://user-images.githubusercontent.com/73895803/193719841-644db40f-988a-4277-b9a2-5c99f87ac2d3.png">
</details>

***

#### 채팅
  * 매니저와 첫 채팅 시작 하기
    * 일정이 있는 날짜 선택 -> 일정 클릭 -> 신청된 서비스 유형과 시간, 메모 내용, 담당 매니저 확인 화면에서 담당 매니저 선택 -> 
    * 매니저 정보 화면으로 이동 -> 화면 우측 상단의 채팅 버튼 클릭 -> 채팅 화면으로 이동
<details>
  <summary>Image</summary>
    
<img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193720521-2a1b7d83-2c7f-4caa-85cb-29f5fa0c1587.png">
</details>


  * 채팅 목록
    * 화면 하단의 채팅 아이콘 클릭 -> 채팅 목록 중 원하는 방을 선택 -> 채팅 화면으로 이동
<details>
  <summary>Image</summary>
    
<img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193720578-232b37cd-9e62-4a40-961d-41184595e5b6.png">
</details>


#### 설정
<details>
  <summary>Image</summary>
    
<img width="271" alt="image" src="https://user-images.githubusercontent.com/73895803/193720884-d1411ec8-dde5-41ad-9627-e709addc612d.png">
</details>


  * 나의 프로필
    * 정보 변경
      * 수정을 원하는 정보를 재 입력 -> 화면 우측 상단의 완료 버튼 클릭
    * 프로필 사진 변경
      * 프로필 이미지 클릭 -> 갤러리로 이동 -> 변경하고자 하는 이미지 선택 -> 프로필 화면으로 이동 -> 화면 우측 상단의 완료 버튼 클릭
    * Image
    <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193720639-a2ca23ce-836d-4738-947d-5a706797d99f.png">
    <img width="274" alt="image" src="https://user-images.githubusercontent.com/73895803/193720763-de74430c-5a99-4c1d-ac33-817429449d12.png">


  * 작성한 리뷰
    * 작성한 리뷰 버튼 클릭 -> 리뷰 삭제를 원한다면 해당 리뷰의 삭제 버튼을 클릭
    * Image
    <img width="273" alt="image" src="https://user-images.githubusercontent.com/73895803/193720799-24e0fcf4-91a1-4dc0-a099-5039ca871ec0.png">


  * 로그아웃
    * 로그아웃 버튼 클릭 -> 로그인 화면으로 이동
   

## Reference
* Pefam
* 도그메이트
