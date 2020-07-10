using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;

public class Popup : MonoBehaviour
{
    public GameObject content;
    public RawImage image;
    public GameObject title;
    //flag는 함수가 중복 호출되는 것을 막기 위한 조건 추가를 위해 사용
    private bool flag = false;

    private void Update()
    {
        if ((CapsuleClick.hitCapsule != "-1") && (flag == false))
        {
            flag = true;
            StartCoroutine(DownloadData());
        }
    }

    //서버로부터 캡슐 소유자의 데이터를 받아 내용을 출력
    IEnumerator DownloadData()
    {
        //특정 캡슐 데이터를 요청
        UnityWebRequest www = UnityWebRequest.Get("http://59.13.134.140:7070/capsules/" + CapsuleClick.hitCapsule);
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

            //이미지,영상 출력을 위한 처리
            var imageData = tmpDataBox.Substring(tmpDataBox.LastIndexOf("\"content\":") + 10);

            //등록이 없는 경우 빈 image 화면 출력
            if (imageData.Contains("null"))
            {
                GameObject.Find("popup").transform.GetChild(3).gameObject.SetActive(true);
            }
            else
            {
                //데이터 형태 처리
                imageData = imageData.Substring(1, imageData.Length - 3);
                var imageArray = imageData.Split('}');

                //데이터 수만큼 오브젝트 생성 및 이미지 적용
                for (int i = 0; i < (imageArray.Length)-1; i++)
                {
                    JObject jimage;
                    
                    //조건에 따라 형태 변형 후 파서
                    if(i == 0)
                    {
                        jimage = JObject.Parse(imageArray[i] + "}");
                    }
                    else
                    {
                        imageArray[i] = imageArray[i].Substring(1);
                        jimage = JObject.Parse(imageArray[i] + "}");
                    }

                    var tmpImage = Instantiate(image, GameObject.Find("images").transform.position, Quaternion.identity, GameObject.Find("images").transform);
                    tmpImage.name = "image" + i;
                    StartCoroutine(DownloadImage(jimage["url"].ToString(), i));
                }
            }
        }
        CapsuleClick.hitCapsule = "-1";
        flag = false;
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
}
