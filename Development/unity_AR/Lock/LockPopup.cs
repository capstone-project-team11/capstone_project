using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;
using System;

public class LockPopup : MonoBehaviour
{
    //flag는 함수가 중복 호출되는 것을 막기 위한 조건 추가를 위해 사용
    private bool flag = false;
    private string capsulenum;

    private void Update()
    {
        capsulenum = CapsuleClick.hitCapsule;
        if ((capsulenum != "-1") && (flag == false))
        {
            flag = true;
            StartCoroutine(DownloadExpire());
            flag = false;
        }
    }

    //서버로부터 캡슐의 데이터를 받아 만료일을 출력
    IEnumerator DownloadExpire()
    {
        /*
        //post 통신 다른 방식
        var bodyJsonString = "{\"capsule_id\":\"" + CapsuleClick.hitCapsule + "\",\"nick_name\":\"nick14\"}";
        var www = new UnityWebRequest(InformationData.serverUrl+ "capsules/id", "POST");
        byte[] bodyRaw = System.Text.Encoding.UTF8.GetBytes(bodyJsonString);
        www.uploadHandler = (UploadHandler)new UploadHandlerRaw(bodyRaw);
        www.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
        www.SetRequestHeader("Content-Type", "application/json");

        yield return www.SendWebRequest();*/

        //서버로 보낼 데이터 추가
        WWWForm form = new WWWForm();
        form.AddField("capsule_id", capsulenum);
        form.AddField("nick_name", InformationData.userNickName);

        //REST API를 이용해 데이터 전송
        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "capsules/id", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            //json형식으로 변환하여 남은 일수를 계산
            var tmpDataBox = www.downloadHandler.text;
            JObject jData = JObject.Parse(tmpDataBox);

            //dday 계산
            var expireDate = jData["expire"].ToString();
            dayNum(expireDate);
        }
    }

    private void dayNum(string day)
    {
        //day = day.Substring(0, 10);
        DateTime time = Convert.ToDateTime(day);
        TimeSpan resultTime = time - DateTime.UtcNow;
        if (resultTime.Days > 0)
        {
            this.GetComponent<Text>().text = "D- " + resultTime.Days;
        }
        else if(resultTime.Days <= 0)
        {
            if (time.Day != DateTime.UtcNow.Day)
            {
                this.GetComponent<Text>().text = "D- 1";
            }
            else if(resultTime.TotalSeconds > 0)
            {
                this.GetComponent<Text>().text = "T- " + resultTime.Hours + ":" + resultTime.Minutes + ":" + resultTime.Seconds;
            }
            else
            {
                this.GetComponent<Text>().text = "opened";
                GameObject.Find(capsulenum).gameObject.GetComponent<Text>().text = "open";
            }
        }
    }
}
