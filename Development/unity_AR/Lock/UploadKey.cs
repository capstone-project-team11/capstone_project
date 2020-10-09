using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class UploadKey : MonoBehaviour
{
    public void uploadUserKey()
    {
        StartCoroutine(uploadData());
    }

    //현재 사용자가 잠금 캡슐에 키 등록
    IEnumerator uploadData()
    {
        WWWForm form = new WWWForm();
        form.AddField("nick_name", InformationData.userNickName);
        form.AddField("capsule_id", KeyList.saveNumber);

        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "capsules/lock/key", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            GameObject.Find("exitExpirePopup").GetComponent<exitExpire>().exitExpirePopup();
            CapsuleClick.hitCapsule = KeyList.saveNumber;
            GameObject.Find("Canvas").transform.GetChild(4).gameObject.SetActive(true);
        }
    }
}
