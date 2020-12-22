use serenity::async_trait;
use serenity::client::{Client, Context, EventHandler};
use serenity::framework::standard::StandardFramework;
use serenity::model::{channel::Message, gateway::Ready};

pub mod commands;
pub mod config;
#[cfg(test)]
pub mod tests;

struct Handler;

#[async_trait]
impl EventHandler for Handler {
    async fn message(&self, ctx: Context, msg: Message) {
        if msg.content == "!ping" {
            if let Err(why) = msg.channel_id.say(&ctx.http, "Pong!").await {
                println!("Error sending message: {:?}", why);
            }
        }
    }

    async fn ready(&self, _: Context, ready: Ready) {
        println!("{} is connected!", ready.user.name);
    }
}

#[tokio::main]
async fn main() {
    let user_commands = StandardFramework::new()
        .configure(|c| c.prefix("!"))
        .group(&commands::users::USERCOMMANDS_GROUP);

    let admin_commands = StandardFramework::new()
        .configure(|c| c.prefix("!"))
        .group(&commands::admins::ADMINCOMMANDS_GROUP);

    let mut client = Client::builder("PLACEHOLDER")
        .event_handler(Handler)
        .framework(user_commands)
        .framework(admin_commands)
        .await
        .expect("Error creating client");

    if let Err(why) = client.start().await {
        println!("Error creating client: {:?}", why);
    }
}
