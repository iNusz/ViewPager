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
