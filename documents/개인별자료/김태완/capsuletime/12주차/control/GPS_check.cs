using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class GPS_check : MonoBehaviour
{
    public GameObject text;
    /*
    private void Awake()
    {
        Input.gyro.enabled = true;
    }
    */
    private void Start()
    {
        //Quaternion q = Input.gyro.attitude;
        //text.GetComponent<Text>().text = normalizedYAngle(q).ToString();
        recentCheck();
    }

    //자이로스코프 z축 각 측정 함수(수정 필요)
    private float normalizedYAngle(Quaternion q)
    {
        Vector3 eulers = q.eulerAngles;
        float zAngle = eulers.z;
        if(zAngle >= 180)
        {
            zAngle -= 360;
        }
        return zAngle;
    }

    //GPS사용이 불가능한 경우 recentCheck 함수를 다시 실행
    private void Update()
    {
        if(!Input.location.isEnabledByUser)
        {
            recentCheck();
        }       
    }

    //GPS 작동 확인
    public void recentCheck()
    {
        //GPS 서비스 확인 체크
        if (!Input.location.isEnabledByUser)
        {
            print("error");          
        }

        //GPS 실행
        Input.location.Start(1f, .1f);

        //GPS 초기화 진행
        int maxWait = 20;
        while (Input.location.status == LocationServiceStatus.Initializing && maxWait > 0)
        {
            maxWait--;
        }

        //시간 내 초기화 실패
        if (maxWait < 1)
        {
            print("Timed out");
        }

        //실행 실패
        if (Input.location.status == LocationServiceStatus.Failed)
        {
            print("Unable to determine device location");
        }
        //실행 완료 후 GPS_convert script 실행
        else
        {
            print("GPS setting clear");
            GetComponent<GPS_convert>().enabled = true;
        }
    }
}
