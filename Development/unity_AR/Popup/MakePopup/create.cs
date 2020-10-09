using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Networking;

public class create : MonoBehaviour
{
    public GameObject capsule;

    //임시 캡슐 생성(3초간 위치를 보여주고 사라짐)
    public void ClickCreate()
    {
        StartCoroutine(Upload());
        transform.parent.gameObject.SetActive(false);
        var point = GameObject.Find("createPoint").transform.position;
        var tmpCapsule = Instantiate(capsule, point, Quaternion.identity);
        tmpCapsule.name = "TMPCAPSULE";
        Destroy(GameObject.Find("TMPCAPSULE"), 3);
    }

    //임시 캡슐 위치 서버 저장
    IEnumerator Upload()
    {
        double tmp_lat = Input.location.lastData.latitude;
        double tmp_lng = Input.location.lastData.longitude;

        //서버로 보낼 데이터 추가
        WWWForm form = new WWWForm();
        form.AddField("nick_name", InformationData.userNickName);
        form.AddField("lat", "" + tmp_lat + "");
        form.AddField("lng", "" + tmp_lng + "");

        //REST API를 이용해 데이터 전송
        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "capsules", form);
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
