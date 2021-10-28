package com.lili.rbtree;


public class RBTree<K extends Comparable<K>,V>{
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    // 树根的引用
    private RBNode root;

    /**
     * 返回根节点
     */
    public RBNode getRoot(){
        return this.root;
    }

    /**
     * 1、获取当前节点的父节点
     */
    private RBNode parentOf(RBNode node){
        if(node != null){
            return node.parent;
        }
        return null;
    }

    /**
     * 2、节点是否为红色
     * @param node
     */
    private boolean isRed(RBNode node){
        if(node != null){
            return node.color == RED;
        }
        return false;
    }
    // 2.1 设置节点为红色
    private void setRed(RBNode node){
        if(node != null){
            node.color = RED;
        }
    }

    /**
     * 3、节点是否为黑色
     * @param node
     */
    private boolean isBlack(RBNode node){
        if(node != null){
            return node.color == BLACK;
        }
        return false;
    }
    // 3.1 设置节点为黑色
    private void setBlack(RBNode node){
        if(node != null){
            node.color = BLACK;
        }
    }

    /**
     * 4、中序打印二叉树
     */
    public void inOrderPrint(){
        inOrderPrint(this.root);
    }
    private void inOrderPrint(RBNode node){
        if(node!=null){
            inOrderPrint(node.left);
            System.out.println("key:"+node.key+",value:"+node.value);
            inOrderPrint(node.right);
        }
    }

    /**
     * 5、左旋 x节点
     *     p                  p
     *     |                  |
     *     x                  y
     *    / \      ---->     / \
     *   lx  y              x  ry
     *      / \            / \
     *     ly ry          lx ly
     *
     * 1.将x的右子节点指向y的左子节点 将y的左子节点的父节点更新为x
     * 2.当x的父节点不为空时，更新y的父节点为x的父节点 ，并将x的父节点指向y
     * 3.将x的父节点更新为y，将y的左子节点更新为x
     */
    private void leftRotate(RBNode x){
        RBNode y = x.right;
        // 1.将x的右子节点指向y的左子节点 将y的左子节点的父节点更新为x
        x.right = y.left;
        if(y.left != null){
            y.left.parent = x;
        }

        // 2.当x的父节点不为空时，更新y的父节点为x的父节点 ，并将x的父节点指向y
        if(x.parent != null){
            y.parent = x.parent;
            if(x == x.parent.left){
                x.parent.left = y;
            }else{
                x.parent.right = y;
            }
        }else{
            this.root = y;
            y.parent = null;
        }

        // 3.将x的父节点更新为y，将y的左子节点更新为x
        x.parent = y;
        y.left = x;
    }

    /**
     * 6、右旋 y节点
     *     p                  p
     *     |                  |
     *     y                  x
     *    / \      ---->     / \
     *   x  ry              lx  y
     *  / \                    / \
     * lx ly                  ly ry
     *
     * 1.将y的左子节点指向x的右子节点 将x的右子节点的父节点更新为y
     * 2.当y的父节点不为空时，更新x的父节点为y的父节点 ，并将y的父节点指向x
     * 3.将y的父节点更新为x，将x的右子节点更新为y
     */
    private void rightRotate(RBNode y){
        RBNode x = y.left;
        // 1.将y的左子节点指向x的右子节点 将x的右子节点的父节点更新为y
        y.left = x.right;
        if(x.right != null){
            x.right.parent = y;
        }

        // 2.当y的父节点不为空时，更新x的父节点为y的父节点 ，并将y的父节点指向x
        if(y.parent != null){
            x.parent = y.parent;
            if(y == y.parent.left){
                y.parent.left = x;
            }else{
                y.parent.right = x;
            }
        }else{
            this.root = x;
            x.parent = null;
        }

        // 3.将y的父节点更新为x，将x的右子节点更新为y
        y.parent = x;
        x.right = y;
    }

    /**
     * 公开的插入方法
     * @param key
     * @param value
     */
    public void insert(K key, V value){
        RBNode node = new RBNode();
        node.setKey(key);
        node.setValue(value);
        // 新节点一定是红色
        node.setColor(RED);
        insert(node);
    }
    private void insert(RBNode node){
        // 第一步 ： 查找当前节点的父节点
        RBNode parent = null;
        RBNode x = this.root;

        while(x != null){
            parent = x;

            // cmp>0 说明node.key>x.key 需要到x的右子树查找
            // cmp==0 说明node.key==x.key 需要进行替换操作
            // cmp<0 说明node.key<x.key 需要到x的左子树查找
            int cmp = node.key.compareTo(x.key);

            if(cmp >0){
                x = x.right;
            }else if(cmp == 0){
                x.setValue(node.getValue());
                return;
            }else{
                x = x.left;
            }
        }

        node.parent = parent;

        if(parent != null){
            // 判断node与parent的key谁大
            int cmp = node.key.compareTo(parent.key);
            if(cmp>0){
                parent.right = node;
            }else{
                parent.left = node;
            }
        }

        // 需要调用红黑树的平衡方法
        insertFixUp(node);

    }

    /**
     * 平衡调整
     * 前提：插入节点必须为红色
     *
     * 情景1：红黑树为空树
     * 　　　　直接插入，并将插入节点变为黑色
     * 情景2：插入节点的key已存在
     * 　　　　更新当前节点的值为插入节点的值
     * 情景3：插入节点的父节点为黑节点
     * 　　　　直接插入即可
     * 情景4：插入节点的父节点为红节点
     * 　　　　情景4.1 ：叔叔节点存在并且为红色
     * 　　　　　　　　　将叔叔节点和父节点改为黑色，将祖父节点改为红色，并将祖父节点设置为当前节点进行后续处理
     * 　　　　情景4.2：叔叔节点不存在或为黑色节点，并且父节点为祖父节点的左子节点
     * 　　　　　　　　情景4.2.1  新插入节点为其父节点的左子节点 （LL情况）
     * 　　　　　　　　　　　　　　将父节点设置为黑色，并将祖父节点设置为红色，再将祖父节点右旋。
     * 　　　　　　　　情景4.2.2 新插入节点为其父节点的右子节点（LR情况）
     * 　　　　　　　　　　　　　　将父节点左旋，变成LL情况，再根据LL情况处理
     * 　　　　情景4.3 ：叔叔节点不存在或为黑色节点，并且父节点为祖父节点的右子节点
     * 　　　　　　　　　和情景4.2处理类似
     * @param node
     */
    private void insertFixUp(RBNode node){
        this.root.setColor(BLACK);

        RBNode parent = parentOf(node);
        RBNode gparent = parentOf(parent);

        // 1.如果父节点是红色（父节点为红色则一定存在祖父节点)
        if(parent != null && isRed(parent)){
            RBNode uncle; // 叔叔节点

            // 父节点再祖父节点左边
            if(parent == gparent.left){
                uncle = gparent.right;
                //情景4.1 ：叔叔节点存在并且为红色（即叔叔节点和父节点都为红色)
                // 将叔叔节点和父节点改为黑色，将祖父节点改为红色，并将祖父节点设置为当前节点进行后续处理
                if(uncle != null && isRed(uncle)){
                    parent.setColor(BLACK);
                    uncle.setColor(BLACK);
                    gparent.setColor(RED);
                    insertFixUp(gparent);
                    return;
                }else{ // 情景4.2：叔叔节点不存在或为黑色节点，并且父节点为祖父节点的左子节点
                    // 新插入节点为其父节点的左子节点 （LL情况）
                    if(node == parent.left){
                        parent.setColor(BLACK);
                        gparent.setColor(RED);
                        rightRotate(gparent);
                        return;
                    }else { // 新插入节点为其父节点的右子节点（LR情况）
                        leftRotate(parent);
                        insertFixUp(parent);
                        return;
                    }
                }
            }else{ // 父节点再祖父节点右边
                uncle = gparent.left;
                //情景4.1 ：叔叔节点存在并且为红色（即叔叔节点和父节点都为红色)
                // 将叔叔节点和父节点改为黑色，将祖父节点改为红色，并将祖父节点设置为当前节点进行后续处理
                if(uncle != null && isRed(uncle)){
                    parent.setColor(BLACK);
                    gparent.setColor(RED);
                    uncle.setColor(BLACK);
                    insertFixUp(gparent);
                    return;
                }else{
                    // 新插入节点为其父节点的左子节点 （LL情况）
                    if(node == parent.right){
                        parent.setColor(BLACK);
                        gparent.setColor(RED);
                        leftRotate(gparent);
                    }else{
                        rightRotate(parent);
                        node.setColor(BLACK);
                        gparent.setColor(RED);
                        leftRotate(gparent);
                    }
                }
            }

        }
        // 2. 如果父节点是黑色 直接插入即可

        // 3. 如果没有父节点
        if(parent == null){
            node.color = BLACK;
            this.root = node;
        }
    }


    static class RBNode<K extends Comparable<K>,V>{
        private RBNode parent;
        private RBNode left;
        private RBNode right;
        private boolean color;
        private K key;
        private V value;

        public RBNode() {
        }

        public RBNode(RBNode parent, RBNode left, RBNode right, boolean color, K key, V value) {
            this.parent = parent;
            this.left = left;
            this.right = right;
            this.color = color;
            this.key = key;
            this.value = value;
        }

        public void setParent(RBNode parent) {
            this.parent = parent;
        }

        public void setLeft(RBNode left) {
            this.left = left;
        }

        public void setRight(RBNode right) {
            this.right = right;
        }

        public void setColor(boolean color) {
            this.color = color;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public RBNode getParent() {
            return parent;
        }

        public RBNode getLeft() {
            return left;
        }

        public RBNode getRight() {
            return right;
        }

        public boolean isColor() {
            return color;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
