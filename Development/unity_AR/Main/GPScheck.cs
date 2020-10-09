using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Android;

public class GPScheck : MonoBehaviour
{
    private bool flag = false;

    private void Awake()
    {
        //GPS 사용권한
        if (!Permission.HasUserAuthorizedPermission(Permission.FineLocation))
            Permission.RequestUserPermission(Permission.FineLocation);
    }

    private void Start()
    {
        Screen.SetResolution(Screen.width, Screen.height, true);
        //guide 팝업창 출력 여부
        if (PlayerPrefs.GetInt("flag") == 1)
        {
            GameObject.Find("guide").gameObject.SetActive(false);
        }
    }

    private void Update()
    {
        if(flag != true)
        {
            check();
        }
    }

    //GPS 작동 확인
    private void check()
    {
        //GPS 서비스 확인 체크
        if (!Input.location.isEnabledByUser)
        {
            print("error");
            return;
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
            return;
        }

        //실행 실패
        if (Input.location.status == LocationServiceStatus.Failed)
        {
            print("Unable to determine device location");
            return;
        }
        //실행 완료
        else
        {
            flag = true;
            print("GPS setting clear");
        }
    }
}
