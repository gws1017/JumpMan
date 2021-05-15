1. 게임의 소개

   제목 : <strong>JumpMan</strong>

   <ul>원게임제목 : 점프킹</ul>
   <ul>"Nexile"에서 만든 게임 , 플랫포머 장르</ul>
   <ul>2019년에 출시한 게임으로 스팀에서 현재 16400원에 판매중(PC)</ul>

   ![image](https://user-images.githubusercontent.com/65538479/113096456-29a9a000-9230-11eb-81a2-b501a9789829.png)

   

   ------

5. 게임 컨셉

   + 2019년 발매된 게임 "JumpKing의 모작"

   + 플랫포머 게임

   + 점프 높이를 조절해서 플레이어가 가장높은 꼭대기까지 오르는 게임

     

6. 개발 범위

   |   내용   |             최소범위             |    추가범위     |
   | :------: | :------------------------------: | :-------------: |
   |  캐릭터  |            이동, 점프            |        X        |
   |    맵    | 타이틀 화면, 게임 화면,엔딩 화면 | 배경 애니메이션 |
   | 오브젝트 |    발판,얼음발판,눈더미 발판     |        X        |
   |  장애물  |              눈보라              |   화면흔들림    |
   
7. 예상 게임 실행 흐름

   ###### 1. 타이틀

   ![image](https://user-images.githubusercontent.com/65538479/113091056-12fe4b80-9226-11eb-8642-611063a4daef.png)

   게임을 실행하면 음악이 흘러나오며 화면을 누르면 게임을 시작할 수 있습니다.

   ######  2. 게임화면

   ![image](https://user-images.githubusercontent.com/65538479/113092985-054ac500-922a-11eb-9186-54e360b4927a.png)

   게임을 처음 시작하면 시작하는 곳으로 점프키를 얼마나 오래 눌렀는지에 따라서 이동거리가 달라집니다.

   

   <img src="https://user-images.githubusercontent.com/65538479/113096049-88bae500-922f-11eb-835b-b8c73f442280.png" alt="image"  />

   이렇게 눈더미 발판도 있고 눈보라에 의해 캐릭터가 밀려나는 부분도 있습니다.

   

   ![image](https://user-images.githubusercontent.com/65538479/113095589-a63b7f00-922e-11eb-839e-3e02cac77fa9.png)

    플레이어는 점프를 잘 조절해서 맨위까지 올라가면 게임은 클리어가 됩니다.

   

8. **개발 일정**

   | 주차  | 구현 항목                                             | 세부 내용                                                    |
   | ----- | ----------------------------------------------------- | ------------------------------------------------------------ |
   | 1주차 | 리소스<span style="color:red">(100%)</span>           | 배경리소스, 사운드리소스 구하기                              |
   | 2주차 | 맵, 오브젝트<span style="color:red">(80%)</span>      | 배경 리소스로 화면에 맵그리기, 발판 구현                     |
   | 3주차 | 캐릭터<span style="color:red">(80%)</span>            | 캐릭터 이동 ,캐릭터 점프                                     |
   | 4주차 | 캐릭터, 오브젝트<span style="color:red">(0%)</span>   | 캐릭터와 발판사이의 충돌처리                                 |
   | 5주차 | 캐릭터 애니메이션<span style="color:red">(70%)</span> | 기본 움직임부터 벽과 부딪혔을때 까지 전체적인 애니메이션 구현 |
   | 6주차 | 중간점검                                              | 부족한점, 오류 있으면 고치기                                 |
   | 7주차 | 장애물 구현                                           | 눈보라(캐릭터가 바람 방향에 따라 밀려남), 화면 흔들림        |
   | 8주차 | 엔딩, 완성                                            | 모든 발판을 그리고 마지막 엔딩까지 구현                      |
| 9주차 | 마무리                                                | 최종 점검 및 추가 구현                                       |
   
   
   
   
   
12.  주차별 커밋 통계

    ![image](https://user-images.githubusercontent.com/65538479/118348242-e14a0580-b583-11eb-88ac-490a274b5d11.png)

    

    | 주차  | 횟수 |
    | :---: | :--: |
    | 1주차 |  1   |
    | 2주차 |  2   |
    | 3주차 |  0   |
    | 4주차 |  1   |
    | 5주차 |  0   |
    | 6주차 |  3   |
    | 7주차 |  3   |
    | 8주차 |      |
    | 9주차 |      |
    | 총계  |  10  |
    
    
    
7. MainGame에 등장하는 게임 오브젝트 설명

   게임 오브젝트는 Player, Background, Joystick 3개의 클래스가 핵심이라고 할 수 있다.

   우선 Joystick 클래스는 중심 좌표와 바깥원과 내부원의 반지름을 받아 생성하고

   그 정보를 토대로 원을 그립니다

   ![image](https://user-images.githubusercontent.com/65538479/118348694-09873380-b587-11eb-8796-fb5545f3859d.png)

   그리고 MainGame에서 onTouchEvent를 이용해 joystick에 터치한위치의 좌표를 받아오고

   원의 중심좌표와의 거리를 계산해 update에서 내부 중심원의 좌표를 업데이트 해줍니다.

   따라서 플레이어는 생성시에 조이스틱을 넘겨받아 멤버로 저장하고 update함수에서 조이스틱의

   움직인 거리에 비례한 이동량에 속도 상수를 곱해 플레이어의 속도를 정합니다.

   

   

   **BackGround**

   ```java
   private int[] maps= {
               R.mipmap.bg_1,
               R.mipmap.bg_2,
       };
   public void nextimg() {
           this.bitmap = GameBitmap.load(maps[++num]);
       }
   public void previmg() {
           this.bitmap = GameBitmap.load(maps[--num]);
       }
   ```

   ```java
   // Player클래스 내부의 Update()에서
   if(y<0) {
               if(bg.isLast()) {
                   y = 900;
                   bg.nextimg();
               }
    }
    else if(y >GameView.view.getHeight()) {
               if(bg.isFirst()) {
                   y = 10;
                   bg.previmg();
               }
     }
   ```

   다음으로 BackGround에서 배경 리소스들을 담고있는 맵의 리스트를 만들고 Player내에서

   Y값이 화면 위를 넘어갈 때 마다 BackGround의 비트맵을 맵리스트에서 참조해서 갱신하여

   맵을 이동하는 방식으로 배경이미지를 교체하고 있습니다.

   

8. 수업에서 추가로 다루었으면 하는 것

   현재 맵 이동 방식을 플레이어의 Y좌표에 따라 배경과 플레이어의 Y좌표를 변경해주는 방식으로

   하고있는데 혹시 뷰포트(화면)을 움직여서 플레이어 좌표 중심으로 따라가는 방법인 

   카메라를 안드로이드 스튜디오에서도 사용가능한지 궁금합니다!

9. 유튜브 발표 링크

   1차발표 링크:https://youtu.be/3bFYYWWzNd4

   2차발표 링크:https://youtu.be/GnancDVb5IA

   

   

