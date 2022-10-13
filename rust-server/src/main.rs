use std::net::TcpStream;
use std::net::TcpListener;

use std::thread;

use std::io::Read;
use std::io::Write;

fn handler(mut stream: TcpStream) {
    println!("client connect: {:?}", stream.local_addr());
                
    let mut buffer = [0; 1024];

    loop {
        match stream.read(&mut buffer) {
            Ok(0) => break,
            Ok(n) => {
                if let Err(e) = stream.write(&buffer[0..n]) {
                    println!("write error: {}", e);
                    break;
                }
            },
            Err(e) => { 
                println!("read error: {}", e);
                break;
            }
        }
    }

    println!("client disconnect: {:?}", stream.local_addr());
}

fn main() {
    let port = 6013;
    let address = format!("127.0.0.1:{}", port);
    
    println!("Starting server on {}", address);

    match TcpListener::bind(&address) {
        Ok(listener) => {
            for stream in listener.incoming() {
                match stream {
                    Ok(stream) => { thread::spawn(move || handler(stream) ); },
                    Err(e) => println!("connection error: {}", e)
                }
            }
        }
        Err(e) => println!("unable to bind port: {}", e)
    }
}
