using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class pop_image : MonoBehaviour
{
    //이미지 url 정의
    private string url0 = "http://img.newspim.com/news/2019/10/14/1910140810190250.jpg";
    private string url1 = "https://blog.hmgjournal.com/images_n/contents/blossomy-cheongju-travel%20(1).jpg";
    private string url2 = "https://i.pinimg.com/originals/16/18/34/161834dfe2b48f29e05bed0c2a2d7c46.jpg";
    private string url3 = "https://joyfesta.kr/uploadedfiles/spot/2019/07/[%ED%81%AC%EA%B8%B0%EB%B3%80%ED%99%98]2017%20%EC%B2%AD%EC%A3%BC%EC%95%BC%ED%96%89%20-%20%EA%B7%BC%EB%8C%80%EB%AC%B8%ED%99%94%EA%B1%B0%EB%A6%AC%20-%206080%ED%92%8D%EA%B2%BD_111808749.jpg";
    private string url4 = "http://118.44.168.218:7070/contents/2.jpeg";

    IEnumerator Start() {
        string[] url = { url0, url1, url2, url3, url4};
        int num = Random.Range(0,5);

        //url에서 이미지 다운로드
        WWW www = new WWW(url[num]);

		//다운로드 완료시까지 대기
		yield return www;

		//오브젝트에 이미지 url 할당
        transform.gameObject.GetComponent<RawImage>().texture = www.texture;
	}
}
