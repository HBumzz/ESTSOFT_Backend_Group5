<img style="width:350px" src="src/main/resources/static/uploads/footer-logo.svg"></img>

# About Salty 🧂

- 절약하는 일상을 공유하는 커뮤니티 SALTY
- 개발 기간: 2024.10.28~2024.11.13

<br>

# Overview

- Salty는 사용자에게 합리적 소비를 통해서 소비에 대한 좀 더 건강한 경험을 제공합니다.
- 게시판 기능을 통해서 일상적인 내용 뿐 아니라, 사용하지 않는 물건을 판매하거나, 필요한 물건을 구매할 수 있으며, 무료 나눔 또한 가능합니다.
- 챌린지와 체크리스트 기능을통해서 좀 더 효율적인 소비를 할 수 있습니다.
- 채팅 기능을 통해서 다른 사용자와의 커뮤니케이션 또한 가능합니다.

<br>

# Description

-

<br>

# Main Features


### FE

1. Websocket을 활용한 1:1 실시간 채팅 기능 구현
   - onopen, onmessage, onclose를 통해 이벤트 핸들링

### BE

1. Spring Security, Oauth2로 회원 인증/ 인가 구현
2. Websocket을 활용한 1:1 실시간 채팅 기능 구현
   - 브로드캐스트 방식 : 세션을 이용해 채팅방에 속한 사용자에게 메세지를 전달
   - websocketHandler를 통해 클라이언트의 메세지를 수신하고, 특정 메세지를 전달

### INFRA



<br>

# Screens

## 회원가입/로그인/프로필

- 소셜로그인 기능을 제공합니다.

<img style="width:200px" src="src/main/resources/static/README/login.jpg"/>

## 메인 화면
- 커뮤니티에서 제공하는 모든 서비스에 접근가능(사용자 정보, 게시판, 챌린지, 체크리스트)

<img style="width:400px" src="src/main/resources/static/README/main.jpg"/>

## 사용자 관련 서비스
#### 1. 사용자 정보(User)
- 사용자의 닉네임, 프로실 사진, 자기소개 와 서비스 이용과 관련된 권한과 포인트등을 확인할 수 있습니다.

<img style="width:400px" src="src/main/resources/static/README/user_info.jpg"/>

#### 2. 프로필 설정
- 사용자의 프로필을 설정합니다. 프로필 사진, 닉네임, 자기소개를 설정할 수 있습니다.

<img style="width:400px" src="src/main/resources/static/README/profileSet.jpg"/>

### 3. 출석 체크
- 출석체크 후 포인트를 지급합니다. 포인트를 기준으로 다른 서비스를 이용할 수 있습니다.

<img style="width:400px" src="src/main/resources/static/README/chool.jpg"/>

## 게시판 서비스(BOARD)
#### 1. 게시판 리스트(일반 유저)
- 모든 게시글을 확인할 수 있습니다.
- 게시글은 크게 일상, 나눔, 중고거래 카테고리로 구분됩니다. 사용자의 필요에 맞는 글을 작성할 수 있습니다.
- 제목뒤에는 게시글의 댓글 수가 표시됩니다.

<img style="width:400px" src="src/main/resources/static/README/boardlist.gif"/>

#### 2. 게시판 리스트(관리자)
- 관리자는 게시글을 관리할 수 있습니다.
- 숨김 처리를 하면 일반 사용자는 해당 게시글을 확인할 수 없습니다.

<img style="width:400px" src="src/main/resources/static/README/admin_board_list_01.gif"/>
<img style="width:400px" src="src/main/resources/static/README/admin_board_list_02.gif"/>

#### 3. 게시글 작성/수정
- 게시글을 작성(수정)할 수 있습니다.
- text, image, video를 첨부할 수 있습니다.
- 마크다운 형식으로도 작성 가능합니다.

<img style="width:400px" src="src/main/resources/static/README/new-article.gif"/>

#### 4. 게시글 상세 보기
- 선택한 게시물의 상세 내용을 확인할 수 있습니다.
- 댓글도 바로 작성할 수 있습니다.
- 좋아요 기능을 통해 게시글과 댓글에 대한 호감을 표현할 수 있습니다.
- 게시글의 수정/삭제, 댓글의 삭제 기능 또한 제공합니다.

<img style="width:400px" src="src/main/resources/static/README/show_article.gif"/>


## 채팅 서비스





## 챌린지 서비스



## 체크 리스트



<br><br>

# Service ENV

- Java 17
- Spring Boot 3.3.5
- MySQL 8.0.39

<br>

# Tools

- GitHub
  - GitHub Flow 전략으로 branch 관리
  - 코드 버전 관리

- Notion
  - 아이디어 회의
  - 컨벤션 정리
  - 산출물 및 공통 문서 관리

- Figma
  - 와이어프레임

- Swagger
  - REST API 문서화
<br>

# Stacks

### FE

<img src="https://img.shields.io/badge/html5-E34F26?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/javascript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black">
<img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white">

### BE

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">

### INFRA

<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
<img src="https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazonaws&logoColor=white">

<br><br>

# Project Outputs

- [기능명세서](https://www.notion.so/h-jun/12deb052dcc680d787b0da0dc84b9c70?v=12deb052dcc681ddb2ca000c72a456eb)
- [Notion](https://www.notion.so/h-jun/5-New-12aeb052dcc6808aabd5e312f1a89076)
- [WireFrame & Design](https://www.figma.com/file/5dLrdcNUMg1AgGSjWIHWkp/%EC%99%80%EC%9D%B4%EC%96%B4%ED%94%84%EB%A0%88%EC%9E%84-%EB%B0%8F-%EB%94%94%EC%9E%90%EC%9D%B8?type=design&node-id=0-1&mode=design)

<br>

# Members

- [정상윤](https://github.com/afteryoon)(BE)
- [최홍준](https://github.com/HBumzz)(BE)
- [이진헌](https://github.com/sodami-hub)(BE)
- [김근아](https://github.com/listoria)(BE)
- [문규찬](https://github.com/gyuchanm)(BE)