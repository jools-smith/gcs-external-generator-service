using System.Runtime.ConstrainedExecution;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace hbk_signer {

  class License {
    public object? Payload { get; set; }
    public byte[]? Signature { get; set; }


    public License Sign() {

      var json = JsonSerializer.Serialize(this.Payload);

      using (var sha = SHA256.Create()) {
        if (sha != null) {
          this.Signature = sha.ComputeHash(Encoding.UTF8.GetBytes(json));
        }
      }

      return this;
    }
  }

  internal class Program {
    static void Main(string[] args) {
      try {
        foreach (var str in args) {
          Console.WriteLine(str);
        }

        if (args.Length == 2) {
          var json = File.ReadAllText(args[0]);

          var license = new License() {
            Payload = JsonSerializer.Deserialize<object>(json)
          };

          File.WriteAllText(args[1], JsonSerializer.Serialize(license.Sign(), new JsonSerializerOptions() {
            WriteIndented = true,
            IndentSize = 2
          }));
        }
        else {
          throw new InvalidDataException("argument count incorrect " + args.Length);
        }
      }
      catch (Exception e) {
        Console.WriteLine(e.GetType().Name + " | " + e.Message);
      }
      finally {
        Console.WriteLine("bye...");
      }
    }
  }
}
