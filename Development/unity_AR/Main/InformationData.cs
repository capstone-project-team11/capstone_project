using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;
using System;

public class InformationData : MonoBehaviour
{
    public static string serverUrl;
    public static string userNickName;
    public static List<string> bothfollowName = new List<string>();
    public static List<string> followName = new List<string>();

    void Start()
    {
        serverUrl = "http://59.13.134.140:7070/";
        var device_width = Screen.width;
        var device_height = Screen.height;
        //해상도에 따른 비율 조정
        if(device_width < 1100 && device_height < 2000)
        {
            GameObject.Find("Canvas").transform.GetChild(0).transform.localPosition = new Vector3(0, 0, 0);
        }
        //테스트용 정보
        /*userNickName = "HANA";
        StartCoroutine(findBothFollow(userNickName));
        StartCoroutine(findFollow(userNickName));
        GetComponent<Rendering>().enabled = true;
        GameObject.Find("Loading").gameObject.SetActive(false);*/
    }
    
    //안드로이드 스튜디오로 부터 유저의 데이터를 전송받은 후 랜더링 진행
    public void setUserNick(string nickName)
    {
        userNickName = nickName;
        StartCoroutine(findBothFollow(userNickName));
        StartCoroutine(findFollow(userNickName));
        GetComponent<Rendering>().enabled = true;
        GameObject.Find("Loading").gameObject.SetActive(false);
    }

    //사용자의 맞팔로우 정보를 저장
    IEnumerator findBothFollow(string nickName)
    {
        UnityWebRequest www = UnityWebRequest.Get(serverUrl + "follow/forfollow/list/" + nickName);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            var stringFollow = www.downloadHandler.text;
            bothfollowName.Add(userNickName);

            //josn 형식으로 변환
            if (stringFollow.Length != 2)
            {
                stringFollow = stringFollow.Substring(1, stringFollow.Length - 2);
                stringFollow = stringFollow.Replace("},","*");
                var tmp_string = stringFollow.Split('*');
                for (int i = 0; i < tmp_string.Length; i++)
                {
                    if(i != tmp_string.Length - 1)
                    {
                        tmp_string[i] += "}";
                    }

                    JObject follower = JObject.Parse(tmp_string[i]);
                    bothfollowName.Add(follower["dest_nick_name"].ToString());
                }
            }
        }
    }

    //팔로우 리스트 정보 저장
    IEnumerator findFollow(string nickName)
    {
        UnityWebRequest www = UnityWebRequest.Get(serverUrl + "follow/followlist/" + nickName);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            var stringFollow = www.downloadHandler.text;
            bothfollowName.Add(userNickName);

            //josn 형식으로 변환
            if (stringFollow.Length != 2)
            {
                stringFollow = stringFollow.Substring(1, stringFollow.Length - 2);
                stringFollow = stringFollow.Replace("},", "*");
                var tmp_string = stringFollow.Split('*');
                for (int i = 0; i < tmp_string.Length; i++)
                {
                    if (i != tmp_string.Length - 1)
                    {
                        tmp_string[i] += "}";
                    }

                    JObject follow = JObject.Parse(tmp_string[i]);
                    followName.Add(follow["nick_name"].ToString());
                }
            }
        }
    }
}
