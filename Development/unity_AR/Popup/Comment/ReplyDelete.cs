using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class ReplyDelete : MonoBehaviour
{
    public void clickDelte()
    {
        var parentName = transform.parent.transform.name;
        StartCoroutine(deleteReply(parentName));
    }

    //댓글 제거
    IEnumerator deleteReply(string name)
    {
        var target = "";

        //댓글,대댓글에 따라 서로 다른 target 생성
        if(name.Contains("reply"))
        {
            var namebox = name.Split('r');
            target = "reply/" + namebox[0];
        }
        else
        {
            var namebox = name.Split('m');
            target = namebox[0];
        }
        
        UnityWebRequest www = UnityWebRequest.Delete(InformationData.serverUrl + "comment/" + target);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            //삭제시 댓글창 재랜더링
            var indexNumber = GameObject.Find("Contents").transform.childCount;
            for (int k = 0; k < indexNumber; k++)
            {
                Destroy(GameObject.Find("Contents").transform.GetChild(k).gameObject);
            }
            Comment.commentFlag = false;
            GameObject.Find("comment").GetComponent<Comment>().clickComment();
        }
    }
}
