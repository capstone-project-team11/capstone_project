using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class Comment : MonoBehaviour
{
    public InputField inputfieldname;
    public GameObject commentBox;
    public GameObject recommentBox;
    //댓글창 활성화시 다른 버튼 활성화 제어
    public static bool commentFlag = false;
    private string commentID;

    public void clickComment()
    {
        if(commentFlag != true)
        {
            commentID = "";
            commentFlag = true;
            transform.GetChild(0).gameObject.SetActive(true);
            StartCoroutine(CommentData());
        }
    }

    //서버로 부터 해당 캡슐의 댓글 정보를 호출하여 출력
    IEnumerator CommentData()
    {
        UnityWebRequest www = UnityWebRequest.Get(InformationData.serverUrl+ "comment/list/"+ Popup.saveCapsuleNumber);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            //데이터를 newtonsof 형식에 맞게 변환
            var tmp_box = www.downloadHandler.text;
            tmp_box = tmp_box.ToString();
            tmp_box = tmp_box.Substring(1, tmp_box.Length - 4);
            tmp_box = tmp_box.Replace("]},", "*");
            var tmp_string = tmp_box.Split('*');
            for(int i = 0; i < tmp_string.Length; i++)
            {
                var tmp = tmp_string[i];
                tmp += "]}";
                JObject userComment = JObject.Parse(tmp);
                makeComment(userComment, commentBox, 0);

                if (userComment["replies"].ToString().Length != 2)
                {
                    var replies = userComment["replies"].ToString();
                    replies = replies.Substring(1, replies.Length - 2);
                    replies = replies.Replace("},", "*");

                    var reply = replies.Split('*');
                    for(int j = 0; j < reply.Length; j++)
                    {
                        var add_char = "}";
                        if(j == reply.Length - 1)
                        {
                            add_char = "";
                        }
                        JObject replyComment = JObject.Parse(reply[j] + add_char);
                        makeComment(replyComment, recommentBox, 1);
                    }
                }
            }
        }
    }

    //댓글 오브젝트를 생성하고 데이터를 입력
    private void makeComment(JObject jobj, GameObject obj, int rflag)
    {
        var replyID = "";
        var commentObj = Instantiate(obj);
        commentObj.transform.SetParent(GameObject.Find("Contents").transform);
        commentObj.transform.GetChild(0).GetComponent<Text>().text = jobj["nick_name"].ToString();
        commentObj.transform.GetChild(1).GetComponent<Text>().text = jobj["comment"].ToString();
        var date = jobj["date_created"].ToString();
        var date1 = date.Substring(0, 10);
        var date2 = date.Substring(10);
        System.DateTime tmp_date = Convert.ToDateTime(date1);
        commentObj.transform.GetChild(3).GetComponent<Text>().text = tmp_date.ToString("yyyy-MM-dd") + date2;

        if (rflag == 0)
        {
            commentID = jobj["comment_id"].ToString() + "main";
        }
        else if(rflag == 1)
        {
            replyID = jobj["reply_id"].ToString() + "reply";
        }

        StartCoroutine(DownloadImage(jobj["user_image_url"].ToString(), commentObj));
        if(jobj["nick_name"].ToString() == InformationData.userNickName)
        {
            commentObj.transform.GetChild(4).gameObject.SetActive(true);
        }
        commentObj.name = replyID + commentID;
    }

    //댓글을 단 사용자의 프로필 사진을 적용
    IEnumerator DownloadImage(string url, GameObject obj)
    {
        UnityWebRequest uwr = UnityWebRequestTexture.GetTexture(url);
        yield return uwr.SendWebRequest();

        if (uwr.isNetworkError || uwr.isHttpError)
        {
            Debug.Log(uwr.error);
        }
        else
        {
            obj.transform.GetChild(2).transform.GetChild(0).GetComponent<RawImage>().texture = DownloadHandlerTexture.GetContent(uwr);
        }
    }

    //댓글창 종료
    public void exitComment()
    {
        var indexNumber = GameObject.Find("Contents").transform.childCount;
        for(int k = 0; k < indexNumber; k++)
        {
            Destroy(GameObject.Find("Contents").transform.GetChild(k).gameObject);
        }
        commentFlag = false;
        inputfieldname.text = "";
        GameObject.Find("Scroll View").transform.GetChild(4).gameObject.SetActive(true);
        GameObject.Find("Scroll View").transform.GetChild(5).gameObject.SetActive(false);
        GameObject.Find("Scroll View").SetActive(false);
    }
}
