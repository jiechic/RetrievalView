#RetrievalView

Detail
------

![image](https://github.com/jiechic/RetrievalView/blob/master/images/readme.gif)

How To Use
----------
```java
lineItem = new LineItem("mParties", "ALL");
    for (String s : mParties) {
        lineItem.addItem(new Item(s, s));
    }
    retrievalView.addRetrieval(lineItem, null);
    retrievalView.show();
    retrievalView.setListener((List<Item> list, boolean isChange) -> {
                keyMonths = null;
                keyParties = null;
                if (isChange) {
                    for (Item item : list) {
                        if (TextUtils.equals(item.getKey(), "mMonths") && !TextUtils.equals(item.getId(), LineItem.DEFAULTID)) {
                            keyMonths = item.getId();
                        }
                        if (TextUtils.equals(item.getKey(), "mParties") && !TextUtils.equals(item.getId(), LineItem.DEFAULTID)) {
                            keyParties = item.getId();
                        }
                    }
                    loadData(keyMonths, keyParties);
                }
            }
    );
```