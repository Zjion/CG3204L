class Client {
  private String name;
  private String ip;
  
  public Client(String name, String ip)
  {
    this.name = name;
    this.ip = ip;
  }
  
  public String getName() {return name;}
  public String getIP() {return ip;}
  
  public void setName(String name) {this.name = name;}
  public void setIP(String IP) {this.ip = ip;}
}
