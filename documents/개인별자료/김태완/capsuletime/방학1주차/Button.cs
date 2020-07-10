using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Networking;
using System;

public class Button : MonoBehaviour
{
    public GameObject capsule;
    private GameObject cancel;
    private GameObject create;
    private GameObject show;
    private GameObject exit;
    private GameObject skin;

    private void Start()
    {
        show = GameObject.Find("button").transform.GetChild(0).gameObject;
        create = GameObject.Find("button").transform.GetChild(1).gameObject;
        cancel = GameObject.Find("button").transform.GetChild(2).gameObject;
        exit = GameObject.Find("button").transform.GetChild(3).gameObject;
        skin = GameObject.Find("Canvas").transform.GetChild(1).gameObject;
    }

    //unity 종료
    public void ClickExit()
    {
#if !UNITY_EDITOR
        System.Diagnostics.Process.GetCurrentProcess().Kill();
#endif
        Input.location.Stop();
        GameObject.Find("ARCore Device").SetActive(false);
        Application.Quit();
    }

    //임시 캡슐 미리보기
    public void ClickShow()
    {
        show.SetActive(false);
        cancel.SetActive(true);
        skin.SetActive(true);
    }

    //임시 캡슐 미리보기 취소
    public void ClickCancel()
    {
        show.SetActive(true);
        cancel.SetActive(false);
        skin.SetActive(false);
    }

    //임시 캡슐 생성(3초간 위치를 보여주고 사라짐)
    public void ClickCreate()
    {
        if(cancel.activeSelf == true)
        {
            StartCoroutine(Upload());
            ClickCancel();
            var point = GameObject.Find("createPoint").transform.position;
            var tmpCapsule = Instantiate(capsule, point, Quaternion.identity);
            tmpCapsule.name = "TMPCAPSULE";
            Destroy(GameObject.Find("TMPCAPSULE"), 3);
        }
    }

    //임시 캡슐 위치 서버 저장
    IEnumerator Upload()
    {
        double tmp_lat = 36.630881;// Input.location.lastData.latitude;
        double tmp_lng = 127.460883;// Input.location.lastData.longitude;

        //서버로 보낼 데이터 추가
        WWWForm form = new WWWForm();
        form.AddField("nick_name", "nick14");
        form.AddField("lat", "" + tmp_lat + "");
        form.AddField("lng", "" + tmp_lng + "");

        //REST API를 이용해 데이터 전송
        UnityWebRequest www = UnityWebRequest.Post("http://59.13.134.140:7070/capsules", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            Debug.Log("Form upload complete!");
        }
    }
}
