# ViewPager
ViewPager를 이용한 기본적인 탭연결

<br/>

## Fragment 생성 및 저장소에 담기

```java
    // 1. ViewPager , TabLayout 위젯연결

    ---...skip...---

    // 2. 프래그먼트 생성
    one = new OneFragment();
    two = new TwoFragment();
    three = new ThreeFragment();
    four = new FourFragment();


    // 3. 프래그먼트를 datas 저장소에 담은 후
    List<Fragment> datas = new ArrayList<>();
    datas.add(one);
    datas.add(two);
    datas.add(three);
    datas.add(four);
```

## Adapter 생성 및 연결

```java
    // 4. 프래그먼트 매니저와 함께 아답터에 전달
    PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),datas);

    // 5. 아답터를 페이저 위젯에 연결
     pager.setAdapter(adapter);
```
###### Adapter Method

```java
class PagerAdapter extends FragmentStatePagerAdapter{

        List<Fragment> datas;

        public PagerAdapter(FragmentManager frag, List<Fragment> datas) {
            super(frag);
            this.datas = datas;
        }

        @Override
        public Fragment getItem(int position) {
            return datas.get(position);
        }

        @Override
        public int getCount() {
            return datas.size();
        }
    }
```

## Tab 또는 Pager가 변경 되었을때 같이 변경 해주는 리스너

```java
    // 6. 페이저가 변경되었을 때 탭을 변경해주는 리스너
    pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

    // 7. 탭이 변경되었을때 페이저를 변경해주는 리스너
    tab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
```

## GoogleMap 불러오기
구글맵 액티비티를 생성해준뒤 google_maps_api.xml에 들어가서
http://console.developers.google.com/flows......곳에 들어가 API키를 받아온다
그후에 manifest에 추가를 시킨다 .

```xml
 <string name="google_maps_key" templateMergeStrategy="preserve" translatable="false">
   YOUR KEY ~~
 </string>
```

## Fragment를 MapView로 쓰기


Fragment manager를 통해 아이디를 가져온다


```java
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_four, container, false);
    // 프래그먼트 안에서 프래그먼트를 가져오는 코드
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    // 맵이 사용할 준비가 되면 onMapReady 함수를 자동으로 호출된다
    mapFragment.getMapAsync(this);

    //프래그먼트가 호출된 상위 액티비티를 가져올수있음 (나를 호출한 액티비티를 가져옴) / getActivity는 나를 가지고있는 액티비티를 뜻한다
    //상위 액티비티의 자원을 사용하기 위해서 Activity를 가져온다
    MainActivity activity = (MainActivity) getActivity();
    manager = activity.getLocationManager();
    return view;
}
```



manager 변수 정의




```java
        LocationManager manager;

        //Context에 있는 Location상수라는걸 알려주는 것이다
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
```





MainActivity에서는 다음 함수를 호출해준다


```java
public LocationManager getLocationManager(){
        return manager;
    }
```






## Map에 Marker찍기


남산타워에 Marker를 찍어보자



```java
@Override
   public void onMapReady(GoogleMap googleMap) {
       mapFrag = googleMap;
       //좌표 생성
       LatLng namsan = new LatLng(37.551968, 126.988500);
       //좌표 적용
       // 1.1 마커생성
       MarkerOptions marker = new MarkerOptions();
       marker.position(namsan); //좌표
       marker.title("남산타워");
       // 1.2 마커를 화면에 그림
       mapFrag.addMarker(marker);
       // 2. 맵의 중심을 해당 좌표로 이동                          좌표   , 줌레벨
       mapFrag.moveCamera(CameraUpdateFactory.newLatLngZoom(namsan,17));
   }
}
```


## GPS 사용을 위한 LocationListener 생성



```java
LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //경도
            double lng = location.getLongitude();
            //위도
            double lat = location.getLatitude();
            //고도
            double alt = location.getAltitude();
            //정확도
            float acc = location.getAccuracy();
            //위치제공자(ISP)
            String provider = location.getProvider();

            //바뀐 현재 좌표
            LatLng current = new LatLng(lat,lng);
            //현재좌표로 카메라를 이동시킬때 // TODO 바로 여기에 전역변수 구글맵이 들어감
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,17));

        }
```


## Fragment 권한설정



#### Manifest 권한 추가


우선 Manifest에 권한을 추가 시켜야 한다


```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
```




#### onResume



현재에 있는 Fragment가 실행되기 직전, 생명주기를 생각하면 onResume에 권한 및 GPS Listener 을 걸어줘야 한다




```java
@Override
    public void onResume() {
        super.onResume();


        //마시멜로 이상버전에서는 런타임 권한 체크여부를 확인해야 한다
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            // GPS 사용을 위한 권한 휙득이 되어 있지 않으면 리스너 해제하지 않는다
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
        }
        // GPS 리스너 등록
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, //위치제공자
                3000, //변경사항 체크 주기 millisecond 단위임
                1, //변경사항 체크 거리 meter단위
                locationListener //나는 locationListener를 쓸꺼야
        );


    }
```




#### onPause



현재에 있는 Fragment가 정지를 했을경우 Fragment , GPS Listener를 해제 시켜야 된다




```java
@Override
    public void onPause() {
        super.onPause();


        //마시멜로 이상버전에서는 런타임 권한 체크여부를 확인해야 한다
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            // GPS 사용을 위한 권한 휙득이 되어 있지 않으면 리스너를 해제하지 않는다
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
        }

        // 리스너 해제 , 프래그먼트 작동 해제
        manager.removeUpdates(locationListener);
    }
```


#### 위치제공자 사용을 위한 권한 처리


##### checkPermission



```java
private final int REQ_PERMISSION = 100;
@TargetApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        //1 권한체크 - 특정권한이 있는지 시스템에 물어본다
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            // 2. 권한이 없으면 사용자에 권한을 달라고 요청
            String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION};
            requestPermissions(permissions, REQ_PERMISSION); // -> 권한을 요구하는 팝업이 사용자 화면에 노출된다
        }
    }
```


##### onRequestPermissionsResult



```java
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQ_PERMISSION) {
        // 3.1 사용자가 승인을 했음
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        } else {
            cancel();
        }
    }
}
```

###### cancel


```java
public void cancel() {

       Toast.makeText(this, "권한요청을 승인하셔야 GPS를 사용할 수 있습니다.", Toast.LENGTH_SHORT).show();
       finish();

   }
```


###### 권한 실행



```java
// 권한을 실행시킨다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
```
