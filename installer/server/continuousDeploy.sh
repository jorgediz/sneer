apt-get -y install openjdk-7-jdk
apt-get -y install ant
cd ~

apt-get -y install junit4
git clone git://github.com/klauswuestefeld/simploy.git
cd simploy
javac -cp .:/usr/share/java/junit4.jar Simploy.java
