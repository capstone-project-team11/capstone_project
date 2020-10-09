using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.Networking;

public class AddFollow : MonoBehaviour
{
    private string nick;

    //follow 버튼 활성화 상태에 따른 추가, 삭제
    public void checkFollowstate()
    {
        nick = GameObject.Find("userName").GetComponent<Text>().text;
        if (InformationData.followName.Contains(nick))
        {
            StartCoroutine(delfollow());
            InformationData.followName.Remove(nick);
        }
        else
        {
            StartCoroutine(postFollow());
            InformationData.followName.Add(nick);
        }
    }

    //follow 추가
    IEnumerator postFollow()
    {
        WWWForm form = new WWWForm();
        form.AddField("nick_name", InformationData.userNickName);
        form.AddField("dest_nick_name", nick);

        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "follow/", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            GameObject.Find("followButton").transform.GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/followButton");
            print("add follow!");
        }
    }
    
    //follow 삭제
    IEnumerator delfollow()
    {
        UnityWebRequest www = UnityWebRequest.Delete(InformationData.serverUrl + "follow?nick_name=" + InformationData.userNickName + "&dest_nick_name=" + nick);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            GameObject.Find("followButton").transform.GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/activefollowButton");
            Debug.Log("Delete follow!");
        }
    }
}
