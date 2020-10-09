using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class Popup : MonoBehaviour
{
    public GameObject content;
    public RawImage image;
    public GameObject title;
    public GameObject daynum;
    //flag는 함수가 중복 호출되는 것을 막기 위한 조건 추가를 위해 사용
    private bool flag = false;
    public static string saveCapsuleNumber = "none";
    public static bool popupActiveFlag = false;

    private void Update()
    {
        if ((CapsuleClick.hitCapsule != "-1") && (flag == false))
        {
            flag = true;
            StartCoroutine(DownloadData());
            saveCapsuleNumber = CapsuleClick.hitCapsule;
            print(saveCapsuleNumber);
            popupActiveFlag = true;
        }
    }

    //서버로부터 캡슐 소유자의 데이터를 받아 내용을 출력
    IEnumerator DownloadData()
    {
        /*
        //post 통신 다른 방식
        var bodyJsonString = "{\"capsule_id\":\"" + CapsuleClick.hitCapsule + "\",\"nick_name\":\"nick14\"}";
        var www = new UnityWebRequest(InformationData.serverUrl+"capsules/id", "POST");
        byte[] bodyRaw = System.Text.Encoding.UTF8.GetBytes(bodyJsonString);
        www.uploadHandler = (UploadHandler)new UploadHandlerRaw(bodyRaw);
        www.downloadHandler = (DownloadHandler)new DownloadHandlerBuffer();
        www.SetRequestHeader("Content-Type", "application/json");

        yield return www.SendWebRequest();*/

        //서버로 보낼 데이터 추가
        WWWForm form = new WWWForm();
        form.AddField("capsule_id", CapsuleClick.hitCapsule);
        form.AddField("nick_name", InformationData.userNickName);

        //REST API를 이용해 데이터 전송
        UnityWebRequest www = UnityWebRequest.Post(InformationData.serverUrl + "capsules/id", form);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            //json형식으로 변환하여 해당 값을 내용과 타이틀에 출력
            var tmpDataBox = www.downloadHandler.text;
            JObject jData = JObject.Parse(tmpDataBox);
            content.GetComponent<Text>().text = jData["text"].ToString();
            title.GetComponent<Text>().text = "#" + jData["title"].ToString();

            //유저 프로필과 닉네임 출력
            GameObject.Find("userName").GetComponent<Text>().text = jData["nick_name"].ToString();
            StartCoroutine(DownloadUserImage(jData["user_id"].ToString()));

            //좋아요 수 출력
            GameObject.Find("like_count").GetComponent<Text>().text = jData["likes"].ToString();

            //사용자가 클릭한 캡슐을 이전에 좋아요를 눌렀는 지 여부에 따라 활성화 상태 적용
            if (jData["like_flag"].ToString() == "1")
            {
                GameObject.Find("popup").transform.GetChild(7).transform.GetChild(0).gameObject.GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/activeLike");
            }
            else if (jData["like_flag"].ToString() == "0")
            {
                GameObject.Find("popup").transform.GetChild(7).transform.GetChild(0).gameObject.GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/unactiveLike");
            }

            //follow 버튼 활성화 결정
            if (jData["nick_name"].ToString() == InformationData.userNickName)
            {
                GameObject.Find("followButton").gameObject.SetActive(false);
            }
            else
            {
                if (InformationData.followName.Contains(jData["nick_name"].ToString()))
                {
                    GameObject.Find("followButton").transform.GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/followButton");
                }
                else
                {
                    GameObject.Find("followButton").transform.GetComponent<RawImage>().texture = Resources.Load<Texture>("Image/activefollowButton");
                }
            }

            //이미지,영상 출력을 위한 처리
            var imageData = jData["content"].ToString();

            //등록이 없는 경우 빈 image 화면 출력
            if (imageData.Length < 3)
            {
                GameObject.Find("popup").transform.GetChild(3).gameObject.SetActive(true);
            }
            else
            {
                //데이터 형태 처리
                imageData = imageData.Substring(1, imageData.Length - 2);
                imageData = imageData.Replace("},","*");
                var imageArray = imageData.Split('*');

                //데이터 수만큼 오브젝트 생성 및 이미지 적용
                for (int i = 0; i < imageArray.Length; i++)
                {
                    JObject jimage;

                    //조건에 따라 형태 변형 후 파서
                    if (i == imageArray.Length - 1)
                    {
                        jimage = JObject.Parse(imageArray[i]);
                    }
                    else
                    {
                        jimage = JObject.Parse(imageArray[i] + "}");
                    }

                    var tmpImage = Instantiate(image, GameObject.Find("images").transform.position, Quaternion.identity, GameObject.Find("images").transform);
                    tmpImage.name = "image" + i;
                    StartCoroutine(DownloadImage(jimage["url"].ToString(), i));
                }
            }

            //캡슐 생성 기간 표시
            dayNum(jData["date_created"].ToString());
        }
        CapsuleClick.hitCapsule = "-1";
        flag = false;
    }

    private void dayNum(string day)
    {
        day = day.Substring(0,10);
        DateTime time = Convert.ToDateTime(day);
        TimeSpan resultTime = DateTime.Now - time;
        daynum.GetComponent<Text>().text = "D+ " + resultTime.Days;
    }
    
    //서버로부터 이미지 데이터 요청
    IEnumerator DownloadImage(string url, int num)
    {
        UnityWebRequest uwr = UnityWebRequestTexture.GetTexture(url);
        yield return uwr.SendWebRequest();

        if (uwr.isNetworkError || uwr.isHttpError)
        {
            Debug.Log(uwr.error);
        }
        else
        {
            GameObject.Find("image" + num).GetComponent<RawImage>().texture = DownloadHandlerTexture.GetContent(uwr);
        }
    }

    //서버로부터 사용자 이미지 데이터 요청
    IEnumerator DownloadUserImage(string userId)
    {
        UnityWebRequest www = UnityWebRequest.Get(InformationData.serverUrl + "users/" + userId);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            var userData = www.downloadHandler.text;
            JObject userInfo = JObject.Parse(userData);
            StartCoroutine(UserProfile(userInfo["image_url"].ToString()));
        }
    }

    //사용자 프로필 이미지 데이터 요청
    IEnumerator UserProfile(string url)
    {
        UnityWebRequest ur = UnityWebRequestTexture.GetTexture(url);
        yield return ur.SendWebRequest();

        if (ur.isNetworkError || ur.isHttpError)
        {
            Debug.Log(ur.error);
        }
        else
        {
            GameObject.Find("userImage").GetComponent<RawImage>().texture = DownloadHandlerTexture.GetContent(ur);
        }
    }
}
