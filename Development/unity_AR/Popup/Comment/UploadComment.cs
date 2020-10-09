using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class UploadComment : MonoBehaviour
{
    public InputField inputfieldname;
    //main 댓글 등록
    public void buttonClick()
    {
        var content = GameObject.Find("InputField").transform.GetChild(2).GetComponent<Text>().text;
        StartCoroutine(uploadMainComment(content));
    }

    //댓글 업로드
    IEnumerator uploadMainComment(string comment)
    {
        WWWForm form = new WWWForm();
        form.AddField("capsule_id", Popup.saveCapsuleNumber);
        form.AddField("nick_name", InformationData.userNickName);
        form.AddField("comment", comment);

        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "comment", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            //업로드시 댓글창 재랜더링
            var indexNumber = GameObject.Find("Contents").transform.childCount;
            for (int k = 0; k < indexNumber; k++)
            {
                Destroy(GameObject.Find("Contents").transform.GetChild(k).gameObject);
            }
            Comment.commentFlag = false;
            GameObject.Find("comment").GetComponent<Comment>().clickComment();
        }
        inputfieldname.text = "";
    }
}
