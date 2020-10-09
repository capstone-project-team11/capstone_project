using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class ExpireButton : MonoBehaviour
{
    public void addKey()
    {
        var capsulename = Popup.saveCapsuleNumber;
        var userNick = InformationData.userNickName;
        StartCoroutine(uploadKey(userNick, capsulename));
    }

    IEnumerator uploadKey(string nick, string number)
    {
        WWWForm form = new WWWForm();
        form.AddField("capsule_id", number);
        form.AddField("nick_name", nick);

        //REST API를 이용해 데이터 전송
        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "capsules/lock/key", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            print("add key complete");
        }
    }
}
