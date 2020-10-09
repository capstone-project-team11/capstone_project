using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class LikeButton : MonoBehaviour
{
    //좋아요 버튼(하트 모양) 클릭시 활성 상태에 따라 다르게 작용
    public void clickLike()
    {
        if(Comment.commentFlag != true)
        {
            var count = System.Convert.ToInt32(GameObject.Find("like_count").GetComponent<Text>().text);
            var like_state = GetComponent<RawImage>().texture.ToString();
            like_state = like_state.Replace(" (UnityEngine.Texture2D)", "");

            //비활성화 상태일 때 +1 후 활성화
            if (like_state == "unactiveLike")
            {
                StartCoroutine(addLike());
                GameObject.Find("like_count").GetComponent<Text>().text = (count + 1).ToString();
                GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/activeLike");
            }

            //활성화 상태일 때 -1 후 비활성화
            else if (like_state == "activeLike")
            {
                StartCoroutine(deleteLike());
                GameObject.Find("like_count").GetComponent<Text>().text = (count - 1).ToString();
                GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/unactiveLike");
            }
        }
    }

    //좋아요 활성화
    IEnumerator addLike()
    {
        WWWForm form = new WWWForm();
        form.AddField("capsule_id", Popup.saveCapsuleNumber);
        form.AddField("nick_name", InformationData.userNickName);

        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "like", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            Debug.Log("Add Like complete!");
        }
    }

    //좋아요 비활성화
    IEnumerator deleteLike()
    {
        UnityWebRequest www = UnityWebRequest.Delete(InformationData.serverUrl + "like?capsule_id="+ Popup.saveCapsuleNumber +"&nick_name="+InformationData.userNickName);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            Debug.Log("Delete Like complete!");
        }
    }
}
