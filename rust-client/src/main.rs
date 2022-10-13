use std::net::TcpStream;
use std::net::TcpListener;

use std::thread;

use std::io::Read;
use std::io::Write;

fn sending_handler(mut stream: TcpStream) {
    let mut buffer = [0; 1024];
    loop {
        match std::io::stdin().read(&mut buffer) {
            Ok(0) => break,
            Ok(n) => {
                if let Err(e) = stream.write(&buffer[0..n]) {
                    println!("sending write error: {}", e);
                    break;
                }
            },
            Err(e) => { 
                println!("sending read error: {}", e);
                break;
            }
        }
    }

    if let Err(e) = stream.shutdown(std::net::Shutdown::Write) {
        println!("error shutting down socket: {}", e);
    }
}

fn receiving_handler(mut stream: TcpStream) {
    let mut buffer = [0; 1024];

    loop {
        match stream.read(&mut buffer) {
            Ok(0) => break,
            Ok(n) => {
                if let Err(e) = std::io::stdout().write(&buffer[0..n]) {
                    println!("receiving write error: {}", e);
                    break;
                }
            },
            Err(e) => { 
                println!("receiving read error: {}", e);
                break;
            }
        }
    }
}


fn main() {
    let port = 6013;
    let address = format!("127.0.0.1:{}", port);
    
    println!("Attempting to connect to server at {}", address);

    match TcpStream::connect(&address) {
        Ok(mut stream) => {

            let stream_read = stream.try_clone().unwrap();
            let stream_write = stream;

            let sending_handle = thread::spawn(move || sending_handler(stream_write));
            let receiving_handle = thread::spawn(move || receiving_handler(stream_read));
            
            receiving_handle.join();
            sending_handle.join();
        }
        Err(e) => println!("unable to connect: {}", e)
    }
}
