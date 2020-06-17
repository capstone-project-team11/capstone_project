using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using static System.Math;
using UnityEngine.UI;

public class GPS_convert : MonoBehaviour
{
    //현재 GPS 위치와 캡슐 GPS 위치 변수
    private double current_x;
    private double current_y;
    private double target_x;
    private double target_y;

    public GameObject point;
    private GameObject tmp_capsule;
    public GameObject text;

    //자이로스코프 z축 각 변수
    private float changeZangle;

    //ar 확인 범위를 좁게 잡아 gps 좌표 변환시 오차가 적음 점을 이용하여 평면 좌표상에서의 좌표를 구함
    private void Start()
    {
        //changeZangle = float.Parse(text.GetComponent<Text>().text);
        //ext.GetComponent<Text>().text = "각도" + changeZangle;
        Invoke("convert_GPS",2f);
    }

    private void Update()
    {
        
    }

    //입력받은 캡슐의 GPS 좌표값을 유니티 내부의 월드 좌표로 변환하여 오브젝트를 해당 좌표에 생성
    //함수를 통해 cm단위의 오차 발생
    public void convert_GPS()
    {
        double distance_x;
        double distance_y;

        current_y = Input.location.lastData.latitude;
        current_x = Input.location.lastData.longitude;
        //현재 고정된 값을 이용해 테스트 추후 json 파일을 통해 값을 받아 변수로 활용
        target_y = 36.626923;
        target_x = 127.464165;
       
        distance_x = Calc(current_y, current_x, current_y, target_x);
        distance_y = Calc(current_y, current_x, target_y, current_x);

        //현재 자이로스코프 오류로 앱 실행시 정면을 기준으로 하며 평면 좌표계를 이용하여 원점을
        //사용자의 위치로 캡슐의 상대적인 위치를 구함(단위 m)
        if(current_x - target_x >= 0)
        {
            if(current_y - target_y >= 0)//제3사분면
            {
                distance_x *= -1;
                distance_y *= -1;
            }
            else if(current_y - target_y < 0)//제2사분면
            {
                distance_x *= -1;
            }
        }

        else if(current_x - target_x < 0)
        {
            if (current_y - target_y >= 0)//제4사분면
            {
                distance_y *= -1;
            }
        }

        //캡슐을 해당위치에 생성
        tmp_capsule = Instantiate(point, new Vector3((float)distance_x,0,(float)distance_y), Quaternion.identity);

    }

    //gps 좌표간 거리 공식
    public double Calc(double lat1, double lon1, double lat2, double lon2)
    {
        double theta, distance;

        theta = lon1 - lon2;
        distance = (Sin(lat1 * PI / 180) * Sin(lat2 * PI / 180)) + (Cos(lat1 * PI / 180) * Cos(lat2 * PI / 180) * Cos(theta * PI / 180));
        distance = Acos(distance);
        distance = distance * 180 / PI;
        distance = distance * 60 * 1.1515;
        distance = distance * 1.609344 * 1000;

        return distance;
    }
}
