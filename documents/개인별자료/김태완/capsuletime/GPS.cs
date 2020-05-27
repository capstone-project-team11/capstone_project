//GPS test code  현재 위치의 위도와 경도를 불러온다.
using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;

public class GPS : MonoBehaviour
{
    bool gpsInit = false;
    LocationInfo currentGPSPosition;
    int gps_connect = 0;

    public Text text_latitude;
    public Text text_longitude;
    public Text text_refresh;

    //unity 시작시 기본값 셋팅
    void Start()
    {
        Input.location.Start(0.5f);
        int wait = 1000; // 기본 값

        // 사용자의 기기가 GPS를 사용 가능한 경우
        if (Input.location.isEnabledByUser)//사용자에 의하여 좌표값을 실행 할 수 있을 경우
        {
            while (Input.location.status == LocationServiceStatus.Initializing && wait > 0)//초기화 진행중이면
            {
                wait--; // 기다리는 시간을 뺀다
            }

            //GPS를 잡는 대기시간
            if (Input.location.status != LocationServiceStatus.Failed)//GPS가 실행중이라면
            {
                gpsInit = true;

                //지연호출,주기호출 RetrieveGPSData 함수를 0.0001초 후에 1초마다 호출
                InvokeRepeating("RetrieveGPSData", 0.0001f, 1.0f);
            }
        }
        else//GPS 사용이 불가능한 경우
        {
            text_latitude.text = "GPS not available";
            text_longitude.text = "GPS not available";
            text_refresh.text = "1";
        }
    }
    void RetrieveGPSData()
    {
        currentGPSPosition = Input.location.lastData;//gps를 데이터를 받습니다.
        text_latitude.text = "위도 " + (currentGPSPosition.latitude).ToString();//위도 값을 받아,텍스트에 출력합니다
        text_longitude.text = "경도 " + (currentGPSPosition.longitude).ToString();//경도 값을 받아, 텍스트에 출력합니다.
        gps_connect++;
        text_refresh.text = "갱신 횟수 : " + gps_connect.ToString();
    }
}
