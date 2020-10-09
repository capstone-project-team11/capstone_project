using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class ReplyUpload : MonoBehaviour
{
    public InputField inputfieldname;
    //대댓글 등록
    public void replyButtonClick()
    {
        var content = GameObject.Find("InputField").transform.GetChild(2).GetComponent<Text>().text;
        StartCoroutine(uploadReplyComment(content));
    }

    //대댓글 업로드
    IEnumerator uploadReplyComment(string comment)
    {
        WWWForm rform = new WWWForm();
        rform.AddField("capsule_id", Popup.saveCapsuleNumber);
        rform.AddField("nick_name", InformationData.userNickName);
        rform.AddField("comment", comment);
        rform.AddField("parent_id", ReplyClick.parentName);

        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl+"comment", rform);
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
            GameObject.Find("Scroll View").transform.GetChild(4).gameObject.SetActive(true);
            GameObject.Find("Scroll View").transform.GetChild(5).gameObject.SetActive(false);
            GameObject.Find("comment").GetComponent<Comment>().clickComment();
        }
        inputfieldname.text = "";
    }
}
